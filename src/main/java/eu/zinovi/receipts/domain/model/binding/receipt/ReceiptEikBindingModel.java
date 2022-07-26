package eu.zinovi.receipts.domain.model.binding.receipt;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class ReceiptEikBindingModel {

    @NotNull(message = "Невалиден ЕИК")
    @Pattern(regexp = "^[0-9]{9}$", message = "Невалиден ЕИК")
    private String eik;
}
