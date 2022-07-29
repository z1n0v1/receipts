package eu.zinovi.receipts.domain.model.binding.admin;

import eu.zinovi.receipts.domain.model.validation.EmailExists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data @NoArgsConstructor @AllArgsConstructor
public class AdminGetUserDetailsBindingModel {
    @NotBlank(message = "Имейл адресът е задължителен")
    @Email(message = "Имейл адресът трябва да е валиден.")
    @EmailExists(message = "Няма такъв потребител.")
    private String email;
}
