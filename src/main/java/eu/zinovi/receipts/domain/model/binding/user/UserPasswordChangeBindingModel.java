package eu.zinovi.receipts.domain.model.binding.user;

import eu.zinovi.receipts.domain.model.validation.FieldMatch;
import eu.zinovi.receipts.domain.model.validation.PasswordMatch;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static eu.zinovi.receipts.util.constants.MessageConstants.*;

@Data
@FieldMatch(first = "newPassword", second = "confirmPassword", message = INVALID_PASSWORD_MATCH)
public class UserPasswordChangeBindingModel {

    @PasswordMatch
    private String oldPassword;

    @NotNull(message = REQUIRED_PASSWORD_NEW)
    @Size(min = 6, max = 200, message = INVALID_PASSWORD_NEW_LENGTH)
    private String newPassword;


    private String confirmPassword;
}
