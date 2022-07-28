package eu.zinovi.receipts.domain.model.binding.item;

import eu.zinovi.receipts.domain.model.validation.ReceiptExists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data @NoArgsConstructor @AllArgsConstructor
public class ItemDeleteBindingModel {

    @NotNull(message = "Номера на касовата бележка е задължителен")
    @ReceiptExists
    private String receiptId;

    // Should check if the position is valid for the given receiptId
    @NotNull(message = "Не е подадено позицията на продукта")
    @Positive(message = "Невалидна позиция")
    private Integer position;
}
