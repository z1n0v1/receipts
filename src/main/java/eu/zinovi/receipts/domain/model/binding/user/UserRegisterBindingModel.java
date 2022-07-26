package eu.zinovi.receipts.domain.model.binding.user;

import eu.zinovi.receipts.domain.model.validation.EmailIsFree;
import eu.zinovi.receipts.domain.model.validation.FieldMatch;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@FieldMatch(first = "password", second = "confirmPassword", message = "Паролите не съвпадат")
public class UserRegisterBindingModel {
    @NotNull(message = "Името е задължително")
    @Size(min = 3, max = 20, message = "Името трябва да е между 3 и 20 символа")
    private String firstName;

    @NotNull(message = "Фамилията е задължителна")
    @Size(min = 3, max = 20, message = "Фамилията трябва да е между 3 и 20 символа")
    private String lastName;

    @NotNull(message = "Псевдонима е задължителен")
    @Size(min = 3, max = 20, message = "Псевдонима трябва да е между 3 и 20 символа")
    private String displayName;

    @EmailIsFree(message = "Това имейл вече съществува")
    @NotBlank(message = "Имейла е задължителен")
    @Email(message = "Невалиден имейл")
    private String email;

    @NotNull(message = "Паролата е задължителна")
    @Size(min = 6, max = 40, message = "Паролата трябва да е между 6 и 40 символа")
    private String password;

    private String confirmPassword;
}
