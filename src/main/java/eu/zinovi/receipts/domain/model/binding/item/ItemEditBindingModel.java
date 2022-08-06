package eu.zinovi.receipts.domain.model.binding.item;

import eu.zinovi.receipts.domain.model.validation.CategoryExists;
import eu.zinovi.receipts.domain.model.validation.ReceiptExists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

import static eu.zinovi.receipts.util.constants.MessageConstants.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class ItemEditBindingModel {

    @NotNull(message = INVALID_RECEIPT_ID)
    @ReceiptExists
    private String receiptId;

    @NotNull(message = REQUIRED_ITEM_POSITION)
    @Positive(message = INVALID_ITEM_POSITION)
    private Integer position;

    @NotBlank(message = REQUIRED_CATEGORY_NAME)
    @CategoryExists
    private String category;

    @NotNull(message = INVALID_ITEM_NAME)
    private String name;

    @NotNull(message = REQUIRED_ITEM_QUANTITY)
    @Positive(message = INVALID_ITEM_QUANTITY)
    private BigDecimal quantity;

    @NotNull(message = REQUIRED_ITEM_PRICE)
    @Positive(message = INVALID_ITEM_PRICE)
    private BigDecimal price;
}
