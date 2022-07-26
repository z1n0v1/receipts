package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.binding.receipt.ReceiptDeleteBindingModel;
import eu.zinovi.receipts.domain.model.service.ReceiptDeleteServiceModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReceiptDeleteBindingToService {
    ReceiptDeleteServiceModel map(ReceiptDeleteBindingModel receiptDeleteBindingModel);
}
