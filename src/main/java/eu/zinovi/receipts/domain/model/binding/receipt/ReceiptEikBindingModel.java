package eu.zinovi.receipts.domain.model.binding.receipt;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data @RequiredArgsConstructor @NoArgsConstructor
public class ReceiptEikBindingModel {

    @NonNull
    @NotNull(message = "Невалиден ЕИК")
    @Pattern(regexp = "^[0-9]{9}$", message = "Невалиден ЕИК")
    private String eik;
}
