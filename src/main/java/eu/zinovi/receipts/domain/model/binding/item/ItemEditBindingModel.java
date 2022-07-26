package eu.zinovi.receipts.domain.model.binding.item;

import eu.zinovi.receipts.domain.model.validation.ReceiptExists;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class ItemEditBindingModel {

    @NotNull
    @ReceiptExists
    private String receiptId;

    @NotNull
    @Positive
    private Integer position;

    @NotBlank
    private String category;

    @NotNull
    private String name;

    @NotNull
    @Positive
    private BigDecimal quantity;

    @NotNull
    @Positive
    private BigDecimal price;
}
