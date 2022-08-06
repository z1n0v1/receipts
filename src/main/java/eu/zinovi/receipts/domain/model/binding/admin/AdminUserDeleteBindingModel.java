package eu.zinovi.receipts.domain.model.binding.admin;

import eu.zinovi.receipts.domain.model.validation.EmailExists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import static eu.zinovi.receipts.util.constants.MessageConstants.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class AdminUserDeleteBindingModel {

    @NotBlank(message = REQUIRED_EMAIL)
    @Email(message = INVALID_EMAIL)
    @EmailExists(message = INVALID_USER)
    private String email;
}
