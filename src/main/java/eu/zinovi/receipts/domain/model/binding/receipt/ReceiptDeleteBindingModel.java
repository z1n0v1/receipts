package eu.zinovi.receipts.domain.model.binding.receipt;

import eu.zinovi.receipts.domain.model.validation.ReceiptExists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ReceiptDeleteBindingModel {

    @ReceiptExists
    String receiptId;
}
