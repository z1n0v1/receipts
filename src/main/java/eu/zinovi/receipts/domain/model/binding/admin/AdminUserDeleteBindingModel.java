package eu.zinovi.receipts.domain.model.binding.admin;

import eu.zinovi.receipts.domain.model.validation.EmailExists;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class AdminUserDeleteBindingModel {

    @NotBlank(message = "Имейл е задължителен")
    @Email(message = "Невалиден имейл")
    @EmailExists(message = "Потребителят не съществува")
    private String email;
}
