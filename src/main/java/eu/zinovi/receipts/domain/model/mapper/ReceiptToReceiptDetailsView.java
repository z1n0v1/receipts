package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.entity.Item;
import eu.zinovi.receipts.domain.model.entity.Receipt;
import eu.zinovi.receipts.domain.model.view.ItemView;
import eu.zinovi.receipts.domain.model.view.ReceiptDetailsView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.ArrayList;
import java.util.Collection;

@Mapper(componentModel = "spring")
public interface ReceiptToReceiptDetailsView {


    @Mappings({
//        @Mapping(source = "id", target = "id"),
            @Mapping(source = "receiptImage.imageUrl", target = "imageUrl"),
        @Mapping(source = "dateOfPurchase", target = "date"),
//        @Mapping(source = "total", target = "total"),
            @Mapping(source = "company.name", target = "companyName"),
            @Mapping(source = "company.address.value", target = "companyAddress"),
            @Mapping(source = "company.eik", target = "companyEik"),
            @Mapping(source = "store.name", target = "storeName"),
            @Mapping(source = "store.address.value", target = "storeAddress"),
            @Mapping(source = "receiptImage.id", target = "imageId"),
            @Mapping(source = "itemsTotal", target = "itemsTotal")
//        @Mapping(source = "items", target = "items")
//            @Mapping(source = "items", target = "items", qualifiedByName = "mapItems")
    })
    ReceiptDetailsView map(Receipt receipt);
/*
    default public ReceiptDetailsView mapRecieptToView(Receipt receipt) {
        ReceiptDetailsView receiptDetailsView = new ReceiptDetailsView();
        receiptDetailsView.setId(receipt.getId());
        receiptDetailsView.setImageUrl(receipt.getReceiptImage().getImageUrl());
        receiptDetailsView.setDate(receipt.getDate());
        receiptDetailsView.setTotal(receipt.getTotal());
        receiptDetailsView.setCompanyName(receipt.getCompany().getName());
        receiptDetailsView.setCompanyAddress(receipt.getCompany().getAddress().getValue());
        receiptDetailsView.setCompanyEik(receipt.getCompany().getEik());
        receiptDetailsView.setStoreName(receipt.getStore().getName());
        receiptDetailsView.setStoreAddress(receipt.getStore().getAddress());
        receiptDetailsView.setItems(ItemToItemView(receipt.getItems()));
        return receiptDetailsView;
    }
*/
    default Collection<ItemView> mapItems(Collection<Item> items) {
        Collection<ItemView> itemViews = new ArrayList<>();
        for (Item item : items) {
            ItemView itemView = new ItemView();
            itemView.setName(item.getName());
            itemView.setQuantity(item.getQuantity());
            itemView.setPrice(item.getPrice());
            itemView.setPosition(item.getPosition());
            itemView.setCategory(item.getCategory() == null ? "" : item.getCategory().getName());
            itemViews.add(itemView);
        }
        return itemViews;
    }

}
