package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.binding.receipt.ReceiptEditBindingModel;
import eu.zinovi.receipts.domain.model.service.ReceiptEditServiceModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ReceiptEditBindingToService {

    @Mappings({
        @Mapping(source = "id", target = "receiptId"),
        @Mapping(source = "eik", target = "companyEik"),
        @Mapping(source = "name", target = "storeName"),
        @Mapping(source = "address", target = "storeAddress"),
        @Mapping(source = "date", target = "receiptDate"),
        @Mapping(source = "total", target = "total")
    })
    ReceiptEditServiceModel map(ReceiptEditBindingModel receiptEditBindingModel);
}
