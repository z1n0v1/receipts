package eu.zinovi.receipts.domain.model.binding.item;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Data
public class ItemDeleteBindingModel {

    @NotNull
    @Pattern(regexp="^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")
    private String receiptId;

    // Should check if the position is valid for the given receiptId
    @NotNull(message = "Не е подадено позицията на продукта")
    @Positive(message = "Невалидна позиция")
    private Integer position;
}
