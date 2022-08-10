package eu.zinovi.receipts.service.impl;

import eu.zinovi.receipts.domain.model.entity.Item;
import eu.zinovi.receipts.domain.model.datatable.FromDatatable;
import eu.zinovi.receipts.domain.model.datatable.ToDatatable;
import eu.zinovi.receipts.domain.model.service.ItemEditServiceModel;
import eu.zinovi.receipts.domain.exception.EntityNotFoundException;
import eu.zinovi.receipts.repository.ItemRepository;
import eu.zinovi.receipts.service.CategoryService;
import eu.zinovi.receipts.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CategoryService categoryService;

    public ItemServiceImpl(ItemRepository itemRepository, CategoryService categoryService) {
        this.itemRepository = itemRepository;
        this.categoryService = categoryService;
    }

    @Override
    public void save(Item item) {
        itemRepository.save(item);
    }

    @Override
    public List<Item> getItems(UUID receiptId) {
        return itemRepository.findByReceiptIdOrderByPosition(receiptId);
    }

    @Override
    public ToDatatable getItemListDatatable(UUID receiptId, FromDatatable fromDatatable) {

        Sort sort = null;

        String[][] sortOrder = fromDatatable.getSortOrder();

        for (String[] sortLine : sortOrder) {
            switch (sortLine[0]) {
                case "position" -> sort = sortLine[1].equals("asc") ?
                        Sort.by(Sort.Direction.ASC, "position") :
                        Sort.by(Sort.Direction.DESC, "position");
                case "name" -> sort = sortLine[1].equals("asc") ?
                        Sort.by(Sort.Direction.ASC, "name") :
                        Sort.by(Sort.Direction.DESC, "name");
                case "category" -> sort = sortLine[1].equals("asc") ?
                        Sort.by(Sort.Direction.ASC, "category") :
                        Sort.by(Sort.Direction.DESC, "category");
                case "quantity" -> sort = sortLine[1].equals("asc") ?
                        Sort.by(Sort.Direction.ASC, "quantity") :
                        Sort.by(Sort.Direction.DESC, "quantity");
                case "price" -> sort = sortLine[1].equals("asc") ?
                        Sort.by(Sort.Direction.ASC, "price") :
                        Sort.by(Sort.Direction.DESC, "price");
                default -> sort = Sort.by(Sort.Direction.ASC, "position");
            }
        }

        Pageable pageable = PageRequest.of(fromDatatable.getStart() / fromDatatable.getLength(),
                fromDatatable.getLength(),
                sort);

        Page<Item> page;
        if(fromDatatable.getSearch().getValue() == null || fromDatatable.getSearch().getValue().isEmpty()) {
            page = itemRepository.findByReceiptId(receiptId,  pageable);
        } else {
            page = itemRepository.findByReceiptIdAndNameContainingOrCategoryNameContaining(receiptId,
                    fromDatatable.getSearch().getValue(),
                    fromDatatable.getSearch().getValue(), pageable);
        }
        ToDatatable toDatatable = new ToDatatable();
        toDatatable.setRecordsTotal(page.getTotalElements());
        toDatatable.setDraw(fromDatatable.getDraw());
        toDatatable.setRecordsFiltered(page.getTotalElements());

        String[][] result = new String[page.getContent().size()][5];
        for (int i = 0; i < page.getContent().size(); i++) {
            result[i][0] = page.getContent().get(i).getPosition().toString();
            result[i][1] = page.getContent().get(i).getCategory() == null ? "" :
                    page.getContent().get(i).getCategory().getName();
            result[i][2] = page.getContent().get(i).getName();
            result[i][3] = page.getContent().get(i).getQuantity().toString();
            result[i][4] = page.getContent().get(i).getPrice().toString();
        }
        toDatatable.setData(result);
        return toDatatable;
    }

    @Override
    @Transactional
    public void updateItem(ItemEditServiceModel itemEditServiceModel) {
        Item item = itemRepository.findByReceiptIdAndPosition(itemEditServiceModel.getReceiptId(),
                itemEditServiceModel.getPosition()).orElseThrow(EntityNotFoundException::new);

        item.setCategory(categoryService.findByName(itemEditServiceModel.getCategory())
                .orElseThrow(EntityNotFoundException::new));

        item.setName(itemEditServiceModel.getName());
        item.setQuantity(itemEditServiceModel.getQuantity());
        item.setPrice(itemEditServiceModel.getPrice());
        itemRepository.save(item);
    }

    @Override
    public void delete(Item item) {
         itemRepository.delete(item);
    }

}
