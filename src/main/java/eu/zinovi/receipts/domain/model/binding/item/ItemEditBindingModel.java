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

@Data @NoArgsConstructor @AllArgsConstructor
public class ItemEditBindingModel {

    @NotNull(message = "Не сте подали номер на касовата бележка")
    @ReceiptExists
    private String receiptId;

    @NotNull(message = "Не сте подали пореден номер артикул")
    @Positive(message = "Пореден номер артикул трябва да е положително число")
    private Integer position;

    @NotBlank(message = "Не сте подали категорията на артикула")
    @CategoryExists
    private String category;

    @NotNull(message = "Не сте подали име на артикула")
    private String name;

    @NotNull(message = "Не сте подали количеството на артикула")
    @Positive(message = "Количеството трябва да е положително число")
    private BigDecimal quantity;

    @NotNull(message = "Не сте подали цена на артикула")
    @Positive(message = "Цената трябва да е положително число")
    private BigDecimal price;
}
