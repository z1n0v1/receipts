package eu.zinovi.receipts.domain.model.binding.admin;

import eu.zinovi.receipts.domain.model.validation.EmailExists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data @NoArgsConstructor @AllArgsConstructor
public class AdminUserDeleteBindingModel {

    @NotBlank(message = "Имейл е задължителен")
    @Email(message = "Невалиден имейл")
    @EmailExists(message = "Потребителят не съществува")
    private String email;
}
