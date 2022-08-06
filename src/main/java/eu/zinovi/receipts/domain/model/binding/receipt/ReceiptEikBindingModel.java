package eu.zinovi.receipts.domain.model.binding.receipt;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static eu.zinovi.receipts.util.constants.MessageConstants.INVALID_EIK;

@Data @RequiredArgsConstructor @NoArgsConstructor
public class ReceiptEikBindingModel {

    @NonNull
    @NotNull(message = INVALID_EIK)
    @Pattern(regexp = "^[0-9]{9}$", message = INVALID_EIK)
    private String eik;
}
