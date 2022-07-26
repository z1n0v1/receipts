package eu.zinovi.receipts.service;

import com.google.cloud.storage.*;
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
import eu.zinovi.receipts.repository.ReceiptImageRepository;
import eu.zinovi.receipts.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReceiptsService {
    private final ItemAddServiceToItem itemAddServiceToItem;
    private final ReceiptToReceiptDetailsView receiptToReceiptDetailsView;
    private final ReceiptToListView receiptToListView;
    private final ReceiptImageRepository receiptImageRepository;
    private final ReceiptRepository receiptRepository;
    private final UserService userService;
    private final CompanyService companyService;
    private final ItemService itemService;
    private final CategoryService categoryService;
    private final StoreService storeService;
    private final Storage storage;

    @Value("${receipts.google.storage.bucket}")
    private String bucket;

    public ReceiptsService(ItemAddServiceToItem itemAddServiceToItem, ReceiptToReceiptDetailsView receiptToReceiptDetailsView, ReceiptToListView receiptToListView, ReceiptImageRepository receiptImageRepository, ReceiptRepository receiptRepository, UserService userService, CompanyService companyService, Storage storage, ItemService itemService, CategoryService categoryService, StoreService storeService) {
        this.itemAddServiceToItem = itemAddServiceToItem;
        this.receiptToReceiptDetailsView = receiptToReceiptDetailsView;
        this.receiptToListView = receiptToListView;
        this.receiptImageRepository = receiptImageRepository;
        this.receiptRepository = receiptRepository;
        this.userService = userService;
        this.companyService = companyService;
        this.storage = storage;
        this.itemService = itemService;
        this.categoryService = categoryService;
        this.storeService = storeService;
    }

    @Transactional
    public List<ReceiptListView> getAllReceiptImagesWithDate() {

//        return receiptImageRepository.findAllB.stream().map(
//                receiptImageToListViewMapper::receiptListView
//        ).collect(Collectors.toList());
        return receiptRepository.findAllByOrderByDateOfPurchaseDesc().stream()
                .map(receiptToListView::map)
                .collect(Collectors.toList());
    }

    public List<ReceiptListView> getReceiptImagesWithDate() {
        return receiptRepository.findByUserOrderByDateOfPurchaseDesc(userService.getCurrentUser()).stream()
                .map(receiptToListView::map)
                .collect(Collectors.toList());
    }

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

        storage.delete(bucket, "receipts/" + receiptImageId + ".jpg");

    }

    @Transactional
    public ReceiptDetailsView getReceiptDetails(UUID id) {

        Receipt receipt = receiptRepository.findById(id).orElse(null);

        if (receipt == null) {
            return null;
        }

        return receiptToReceiptDetailsView.map(receipt);

        /*
        ReceiptDetailsView receiptDetailsView = new ReceiptDetailsView();
        ReceiptImage receiptImage = receiptImageRepository.findById(id).orElse(null);

        receiptDetailsView.setImageUrl(receiptImage.getImageUrl());
        receiptDetailsView.setId(receiptImage.getId());

        return receiptDetailsView;

         */
    }

    @Transactional
    public void deleteItem(ItemDeleteServiceModel itemDeleteServiceModel) {
        Receipt receipt = receiptRepository.findById(itemDeleteServiceModel.getReceiptId())
                .orElseThrow(EntityNotFoundException::new);
        if (itemDeleteServiceModel.getPosition() > receipt.getItems().size()) {
            throw new EntityNotFoundException();
        }

        BigDecimal itemsTotal = BigDecimal.ZERO;

        for (int i = 0; i < receipt.getItems().size(); i++) {
            if (receipt.getItems().get(i).getPosition().equals(itemDeleteServiceModel.getPosition())) {
                itemService.delete(receipt.getItems().get(i));
                continue;
            }
            itemsTotal = itemsTotal.add(receipt.getItems().get(i).getPrice());
        }

        for (int i = 0; i < receipt.getItems().size(); i++) {
            if (receipt.getItems().get(i).getPosition() > itemDeleteServiceModel.getPosition()) {
                receipt.getItems().get(i).setPosition(receipt.getItems().get(i).getPosition() - 1);
        }
    }

        receipt.setItemsTotal(itemsTotal);
        receiptRepository.save(receipt);
}


    @Transactional
    public void addItem(ItemAddServiceModel itemAddServiceModel) {
        Receipt receipt = receiptRepository.findById(itemAddServiceModel.getReceiptId())
                .orElseThrow(EntityNotFoundException::new);

        Item item = itemAddServiceToItem.map(itemAddServiceModel);
//        Item item = new Item();

        item.setCategory(categoryService.findByName(itemAddServiceModel.getCategory())
                .orElseThrow(EntityNotFoundException::new));

//        item.setName(itemAddServiceModel.getName());
//        item.setQuantity(itemAddServiceModel.getQuantity());
//        item.setPrice(itemAddServiceModel.getPrice());

        item.setPosition(receipt.getItems().size() + 1);
        item.setReceipt(receipt);

        itemService.save(item);
        receipt.getItems().add(item);
        receipt.setItemsTotal(receipt.getItemsTotal().add(item.getPrice()));
        receiptRepository.save(receipt);
    }

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

    @Transactional
    public ToDatatable getReceipts(FromDatatable fromDatatable) {
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

    public boolean existsById(UUID id) {
        return receiptRepository.existsById(id);
    }
}
