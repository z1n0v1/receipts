package eu.zinovi.receipts.domain.model.binding.user;

import eu.zinovi.receipts.domain.model.validation.FieldMatch;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static eu.zinovi.receipts.util.constants.MessageConstants.*;

@Data
@FieldMatch(first = "password", second = "confirmPassword", message = INVALID_PASSWORD_MATCH)
public class UserPasswordSetBindingModel {
    @NotNull(message = REQUIRED_PASSWORD)
    @Size(min = 6, max = 200, message = INVALID_PASSWORD_LENGTH)
    private String password;

    private String confirmPassword;
}
