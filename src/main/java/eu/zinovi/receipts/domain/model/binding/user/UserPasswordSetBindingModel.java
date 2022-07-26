package eu.zinovi.receipts.domain.model.binding.user;

import eu.zinovi.receipts.domain.model.validation.FieldMatch;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@FieldMatch(first = "password", second = "confirmPassword", message = "Паролите не съвпадат")
public class UserPasswordSetBindingModel {
    @NotNull(message = "Паролата не може да бъде празна")
    @Size(min = 6, max = 200, message = "Паролата трябва да бъде поне 6 символа")
    private String password;

    private String confirmPassword;
}
