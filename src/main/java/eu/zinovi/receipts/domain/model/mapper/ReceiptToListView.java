package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.entity.Category;
import eu.zinovi.receipts.domain.model.entity.Receipt;
import eu.zinovi.receipts.domain.model.view.ReceiptListView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ReceiptToListView {

    @Mappings({
            @Mapping(source = "id", target = "receiptId"),
            @Mapping(source = "receiptImage.imageUrl", target = "imageUrl"),
            @Mapping(source = "dateOfPurchase", target = "addedOn"),
            @Mapping(source = "company.name", target = "companyName"),
            @Mapping(source = "store.name", target = "storeName")
    })
    ReceiptListView map(Receipt receipt);

//    @Mappings({
//            @Mapping(source = "name", target = "of")
//    })
//    String map(Category category);
//    String

    default String map(Category category) {
        return category == null ? "" : category.getName();
    }
}
