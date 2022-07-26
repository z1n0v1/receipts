package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.entity.Receipt;
import eu.zinovi.receipts.domain.model.view.admin.AdminReceiptView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ReceiptToAdminView {

    @Mappings({
            @Mapping(source = "receiptImage.id", target = "imageId"),
            @Mapping(source = "receiptImage.imageUrl", target = "imageUrl"),

            @Mapping(source = "company.eik", target = "companyEik"),
            @Mapping(source = "store.name", target = "storeName"),
            @Mapping(source = "store.address.value", target = "storeAddress"),
            @Mapping(source = "itemsTotal", target = "itemsTotal")
    })
    AdminReceiptView map(Receipt receipt);
}
