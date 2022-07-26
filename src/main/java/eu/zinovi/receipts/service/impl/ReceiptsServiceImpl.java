package eu.zinovi.receipts.service.impl;

import eu.zinovi.receipts.domain.exception.ReceiptProcessException;
import eu.zinovi.receipts.domain.model.datatable.FromDatatable;
import eu.zinovi.receipts.domain.model.datatable.ToDatatable;
import eu.zinovi.receipts.domain.model.entity.*;
import eu.zinovi.receipts.domain.model.mapper.*;
import eu.zinovi.receipts.domain.model.service.ItemAddServiceModel;
import eu.zinovi.receipts.domain.model.service.ItemDeleteServiceModel;
import eu.zinovi.receipts.domain.model.service.ReceiptDeleteServiceModel;
import eu.zinovi.receipts.domain.model.service.ReceiptEditServiceModel;
import eu.zinovi.receipts.domain.model.view.ReceiptDetailsView;
import eu.zinovi.receipts.domain.model.view.ReceiptListView;
import eu.zinovi.receipts.domain.exception.EntityNotFoundException;
import eu.zinovi.receipts.domain.model.view.admin.AdminReceiptView;
import eu.zinovi.receipts.repository.ReceiptImageRepository;
import eu.zinovi.receipts.repository.ReceiptRepository;
import eu.zinovi.receipts.service.*;
import eu.zinovi.receipts.util.ReceiptProcessApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static eu.zinovi.receipts.util.ImageProcessing.graphicallyProcessReceipt;
import static eu.zinovi.receipts.util.ImageProcessing.readQRCode;

@Service
public class ReceiptsServiceImpl implements ReceiptsService {
    private final ItemAddServiceToItem itemAddServiceToItem;
    private final ReceiptToReceiptDetailsView receiptToReceiptDetailsView;
    private final ReceiptToListView receiptToListView;
    private final ReceiptToAdminView receiptToAdminView;
    private final ReceiptImageRepository receiptImageRepository;
    private final ReceiptRepository receiptRepository;
    private final UserService userService;
    private final MessagingService messagingService;
    private final ReceiptProcessService receiptProcessService;
    private final CompanyService companyService;
    private final ItemService itemService;
    private final CategoryService categoryService;
    private final StoreService storeService;
    private final ReceiptProcessApi receiptProcessApi;

    public ReceiptsServiceImpl(
            ItemAddServiceToItem itemAddServiceToItem,
            ReceiptToReceiptDetailsView receiptToReceiptDetailsView,
            ReceiptToListView receiptToListView,
            ReceiptToAdminView receiptToAdminView,
            ReceiptImageRepository receiptImageRepository,
            ReceiptRepository receiptRepository,
            UserService userService,
            MessagingService messagingService,
            ReceiptProcessService receiptProcessService,
            CompanyService companyService,
            ItemService itemService,
            CategoryService categoryService,
            StoreService storeService,
            ReceiptProcessApi receiptProcessApi) {
        this.itemAddServiceToItem = itemAddServiceToItem;
        this.receiptToReceiptDetailsView = receiptToReceiptDetailsView;
        this.receiptToListView = receiptToListView;
        this.receiptToAdminView = receiptToAdminView;
        this.receiptImageRepository = receiptImageRepository;
        this.receiptRepository = receiptRepository;
        this.userService = userService;
        this.messagingService = messagingService;
        this.receiptProcessService = receiptProcessService;
        this.companyService = companyService;
        this.itemService = itemService;
        this.categoryService = categoryService;
        this.storeService = storeService;

        this.receiptProcessApi = receiptProcessApi;
    }

    @Override
    @Transactional
    public UUID uploadReceipt(MultipartFile file) throws ReceiptProcessException {

        String fileExtension = null;
        String fileName = file.getOriginalFilename();

        if (file.getContentType() != null) {
            String[] contentType = file.getContentType().split("/");
            if (contentType.length > 1) {
                fileExtension = contentType[1];
            }
        }

        if (fileExtension == null || file.isEmpty() || file.getSize() > 10000000 ||
                (!fileExtension.equals("jpeg")
                        && !fileExtension.equals("png")
                        && !fileExtension.equals("jpg"))) {
            throw new ReceiptProcessException("Неподдържан файлов формат.");
        }

        BufferedImage image;
        try {
            image = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            throw new ReceiptProcessException("Неподдържан файлов формат.");
        }

        String qrCode = readQRCode(image);

        messagingService.sendMessage(fileName + ": Обработка...");
        ByteArrayInputStream processedImageStream = graphicallyProcessReceipt(image);

        ReceiptImage receiptImage = new ReceiptImage();
        receiptImage.setIsProcessed(false);
        receiptImage.setAddedOn(LocalDateTime.now());
        receiptImage.setUser(userService.getCurrentUser());
        receiptImageRepository.save(receiptImage);

        receiptProcessApi.setReceiptId(receiptImage.getId());

        receiptImage.setImageUrl(receiptProcessApi.uploadReceipt(processedImageStream));

        messagingService.sendMessage(fileName + ": Анализ...");
        String polyJson = receiptProcessApi.doOCR();

        receiptImageRepository.save(receiptImage);

        return receiptProcessService.parseReceipt(polyJson, receiptImage, fileName, qrCode);
    }

    @Override
    public boolean existsById(UUID id) {
        return receiptRepository.existsById(id);
    }

    @Override
    @Transactional
    public ReceiptDetailsView getReceiptDetails(UUID id) {

        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        return receiptToReceiptDetailsView.map(receipt);
    }

    @Override
    public List<ReceiptListView> getUserReceiptsWithDate() {
        return receiptRepository.findByUserOrderByDateOfPurchaseDesc(userService.getCurrentUser()).stream()
                .map(receiptToListView::map)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ReceiptListView> getAllReceiptsWithDate() {

        return receiptRepository.findAllByOrderByDateOfPurchaseDesc().stream()
                .map(receiptToListView::map)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ToDatatable getUserReceipts(FromDatatable fromDatatable) {
        Sort sort = null;

        String[][] sortOrder = fromDatatable.getSortOrder();

        for (String[] sortLine : sortOrder) {
            switch (sortLine[0]) {
                case "total" -> sort = sortLine[1].equals("asc") ?
                        Sort.by(Sort.Direction.ASC, "total") :
                        Sort.by(Sort.Direction.DESC, "total");
                case "dateOfPurchase" -> sort = sortLine[1].equals("asc") ?
                        Sort.by(Sort.Direction.ASC, "dateOfPurchase") :
                        Sort.by(Sort.Direction.DESC, "dateOfPurchase");
                default -> sort = Sort.by(Sort.Direction.DESC, "dateOfPurchase");
            }
        }

        Pageable pageable = PageRequest.of(fromDatatable.getStart() / fromDatatable.getLength(),
                fromDatatable.getLength(),
                sort);

        Page<Receipt> page;
        User user = userService.getCurrentUser();

        if (fromDatatable.getSearch().getValue() == null || fromDatatable.getSearch().getValue().isEmpty()) {

            page = receiptRepository.findByUser(user, pageable);

        } else {

            page = receiptRepository.findByUserAndCompanyNameContainingOrStoreNameContaining(
                    user,
                    fromDatatable.getSearch().getValue(),
                    fromDatatable.getSearch().getValue(),
                    pageable);
        }
        ToDatatable toDatatable = new ToDatatable();
        toDatatable.setRecordsTotal(page.getTotalElements());
        toDatatable.setDraw(fromDatatable.getDraw());
        toDatatable.setRecordsFiltered(page.getTotalElements());

        String[][] result = new String[page.getContent().size()][6];
        for (int i = 0; i < page.getContent().size(); i++) {
            result[i][0] = page.getContent().get(i).getId().toString();
            result[i][1] = page.getContent().get(i).getDateOfPurchase()
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
            result[i][2] = page.getContent().get(i).getTotal().toString() + " лв.";

            Set<String> categories = new HashSet<>();
            for (Item item : page.getContent().get(i).getItems()) {
                categories.add(item.getCategory().getName());
            }
            result[i][3] = String.join(", ", categories);

            result[i][4] = page.getContent().get(i).getCompany().getName();
            result[i][5] = page.getContent().get(i).getStore().getName();
        }
        toDatatable.setData(result);
        return toDatatable;
    }

    @Override
    @Transactional
    public ToDatatable getAllReceipts(FromDatatable fromDatatable) {
        Sort sort = null;

        String[][] sortOrder = fromDatatable.getSortOrder();

        for (String[] sortLine : sortOrder) {
            switch (sortLine[0]) {
                case "total" -> sort = sortLine[1].equals("asc") ?
                        Sort.by(Sort.Direction.ASC, "total") :
                        Sort.by(Sort.Direction.DESC, "total");
                case "dateOfPurchase" -> sort = sortLine[1].equals("asc") ?
                        Sort.by(Sort.Direction.ASC, "dateOfPurchase") :
                        Sort.by(Sort.Direction.DESC, "dateOfPurchase");
                default -> sort = Sort.by(Sort.Direction.DESC, "dateOfPurchase");
            }
        }

        Pageable pageable = PageRequest.of(fromDatatable.getStart() / fromDatatable.getLength(),
                fromDatatable.getLength(),
                sort);

        Page<Receipt> page;

        if (fromDatatable.getSearch().getValue() == null || fromDatatable.getSearch().getValue().isEmpty()) {

            page = receiptRepository.findAll(pageable);

        } else {

            page = receiptRepository.findAllByCompanyNameContainingOrStoreNameContaining(
                    fromDatatable.getSearch().getValue(),
                    fromDatatable.getSearch().getValue(),
                    pageable);
        }
        ToDatatable toDatatable = new ToDatatable();
        toDatatable.setRecordsTotal(page.getTotalElements());
        toDatatable.setDraw(fromDatatable.getDraw());
        toDatatable.setRecordsFiltered(page.getTotalElements());

        String[][] result = new String[page.getContent().size()][6];
        for (int i = 0; i < page.getContent().size(); i++) {
            result[i][0] = page.getContent().get(i).getId().toString();
            result[i][1] = page.getContent().get(i).getDateOfPurchase()
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
            result[i][2] = page.getContent().get(i).getTotal().toString() + " лв.";

            Set<String> categories = new HashSet<>();
            for (Item item : page.getContent().get(i).getItems()) {
                categories.add(item.getCategory().getName());
            }
            result[i][3] = String.join(", ", categories);

            result[i][4] = page.getContent().get(i).getCompany().getName();
            result[i][5] = page.getContent().get(i).getStore().getName();
        }
        toDatatable.setData(result);
        return toDatatable;
    }

    @Override
    @Transactional
    public AdminReceiptView getAdminReceipt(UUID receiptId) {
        Receipt receipt = receiptRepository.findById(receiptId).orElseThrow(EntityNotFoundException::new);

        return receiptToAdminView.map(receipt);
    }

    @Override
    @Transactional
    public void saveReceipt(ReceiptEditServiceModel receiptEditServiceModel) {
        Receipt receipt = receiptRepository.findById(receiptEditServiceModel.getReceiptId())
                .orElseThrow(EntityNotFoundException::new);

        receipt.setDateOfPurchase(receiptEditServiceModel.getReceiptDate());
        receipt.setTotal(receiptEditServiceModel.getTotal());

        Company company = companyService.findByEik(receiptEditServiceModel.getCompanyEik());
        receipt.setCompany(company);

        Store store = storeService.findByNameAndAddressAndCompany(receiptEditServiceModel.getStoreName(),
                receiptEditServiceModel.getStoreAddress(),
                company);

        receipt.setStore(store);

        receiptRepository.save(receipt);
    }

    @Override
    @Transactional
    public void deleteReceipt(ReceiptDeleteServiceModel receiptDeleteServiceModel) {

        Receipt receipt = receiptRepository.findById(
                        receiptDeleteServiceModel.getReceiptId())
                .orElseThrow(EntityNotFoundException::new);

        for (Item item : receipt.getItems()) {
            itemService.delete(item);
        }

        String receiptImageId = receipt.getReceiptImage().getId().toString();

        receiptImageRepository.delete(receipt.getReceiptImage());

        receiptRepository.delete(receipt);

        receiptProcessApi.deleteReceipt(receiptImageId);
        receiptProcessApi.close();

    }

    @Override
    @Transactional
    public void addItem(ItemAddServiceModel itemAddServiceModel) {
        Receipt receipt = receiptRepository.findById(itemAddServiceModel.getReceiptId())
                .orElseThrow(EntityNotFoundException::new);

        Item item = itemAddServiceToItem.map(itemAddServiceModel);
        item.setCategory(categoryService.findByName(itemAddServiceModel.getCategory())
                .orElseThrow(EntityNotFoundException::new));

        item.setPosition(receipt.getItems().size() + 1);
        item.setReceipt(receipt);

        itemService.save(item);
        receipt.getItems().add(item);
        receipt.setItemsTotal(receipt.getItemsTotal().add(item.getPrice()));
        receiptRepository.save(receipt);
    }

    @Override
    @Transactional
    public void deleteItem(ItemDeleteServiceModel itemDeleteServiceModel) {
        Receipt receipt = receiptRepository.findById(itemDeleteServiceModel.getReceiptId())
                .orElseThrow(EntityNotFoundException::new);
        if (itemDeleteServiceModel.getPosition() > receipt.getItems().size()) {
            throw new EntityNotFoundException();
        }

        BigDecimal itemsTotal = BigDecimal.ZERO;


        for (Item item : receipt.getItems()) {
            if (item.getPosition().equals(itemDeleteServiceModel.getPosition())) {
                itemService.delete(item);
                continue;
            }
            itemsTotal = itemsTotal.add(item.getPrice());
        }

        for (Item item : receipt.getItems()) {
            if (item.getPosition() > itemDeleteServiceModel.getPosition()) {
                item.setPosition(item.getPosition() - 1);
            }
        }

        receipt.setItemsTotal(itemsTotal);
        receiptRepository.save(receipt);
    }
}
