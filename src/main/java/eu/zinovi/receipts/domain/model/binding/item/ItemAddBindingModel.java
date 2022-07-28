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
public class ItemAddBindingModel {
    @NotNull(message = "Номера на бележката е задължителен")
    @ReceiptExists
    private String receiptId;

    @NotBlank(message = "Категорията е задължителна")
    @CategoryExists
    private String category;

    @NotNull(message = "Невалидно име на артикул")
    private String name;

    @NotNull(message = "Невалидно количеество на артикул")
    @Positive(message = "Невалидно количество на артикул")
    private BigDecimal quantity;

    @NotNull(message = "Невалидна цена на артикул")
    @Positive(message = "Невалидна цена на артикул")
    private BigDecimal price;
}
