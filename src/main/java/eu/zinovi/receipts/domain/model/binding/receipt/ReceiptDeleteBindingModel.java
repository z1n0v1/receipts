package eu.zinovi.receipts.domain.model.binding.receipt;

import eu.zinovi.receipts.domain.model.validation.ReceiptExists;
import lombok.Data;

@Data
public class ReceiptDeleteBindingModel {

    @ReceiptExists
    String receiptId;
}
