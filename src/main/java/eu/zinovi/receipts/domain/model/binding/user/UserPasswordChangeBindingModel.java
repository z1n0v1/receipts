package eu.zinovi.receipts.domain.model.binding.user;

import eu.zinovi.receipts.domain.model.validation.FieldMatch;
import eu.zinovi.receipts.domain.model.validation.PasswordMatch;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@FieldMatch(first = "newPassword", second = "confirmPassword", message = "Passwords must match")
public class UserPasswordChangeBindingModel {

    @PasswordMatch
    private String oldPassword;

    @NotNull(message = "Новата парола не може да бъде празна")
    @Size(min = 6, max = 200, message = "Новата парола трябва да бъде поне 6 символа")
    private String newPassword;


    private String confirmPassword;
}
