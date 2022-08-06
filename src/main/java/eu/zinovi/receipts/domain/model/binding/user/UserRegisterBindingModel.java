package eu.zinovi.receipts.domain.model.binding.user;

import eu.zinovi.receipts.domain.model.validation.EmailIsFree;
import eu.zinovi.receipts.domain.model.validation.FieldMatch;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static eu.zinovi.receipts.util.constants.MessageConstants.*;

@Data
@FieldMatch(first = "password", second = "confirmPassword", message = INVALID_PASSWORD_MATCH)
public class UserRegisterBindingModel {
    @NotNull(message = REQUIRED_NAME)
    @Size(min = 3, max = 20, message = INVALID_NAME_LENGTH)
    private String firstName;

    @NotNull(message = REQUIRED_LAST_NAME)
    @Size(min = 3, max = 20, message = INVALID_LAST_NAME_LENGTH)
    private String lastName;

    @NotNull(message = REQUIRED_DISPLAY_NAME)
    @Size(min = 3, max = 20, message = INVALID_DISPLAY_NAME_LENGTH)
    private String displayName;

    @EmailIsFree
    @NotBlank(message = REQUIRED_EMAIL)
    @Email(message = INVALID_EMAIL)
    private String email;

    @NotNull(message = REQUIRED_PASSWORD)
    @Size(min = 6, max = 200, message = INVALID_PASSWORD_LENGTH)
    private String password;

    private String confirmPassword;
}
