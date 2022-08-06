package eu.zinovi.receipts.domain.model.binding.item;

import eu.zinovi.receipts.domain.model.validation.ReceiptExists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import static eu.zinovi.receipts.util.constants.MessageConstants.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class ItemDeleteBindingModel {

    @NotNull(message = REQUIRED_RECEIPT_ID)
    @ReceiptExists
    private String receiptId;

    // Should check if the position is valid for the given receiptId
    @NotNull(message = REQUIRED_ITEM_POSITION)
    @Positive(message = INVALID_ITEM_POSITION)
    private Integer position;
}
