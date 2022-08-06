package eu.zinovi.receipts.domain.model.binding.user;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static eu.zinovi.receipts.util.constants.MessageConstants.*;

@Data
public class UserDetailsBindingModel {

    @NotNull(message = REQUIRED_NAME)
    @Size(min = 2, max = 200, message = "Името трябва да е поне 2 символа")
    private String firstName;

    @NotNull(message = REQUIRED_LAST_NAME)
    @Size(min = 2, max = 200, message = "Фамилията трябва да е поне 2 символа")
    private String lastName;

    @NotNull(message = REQUIRED_DISPLAY_NAME)
    @Size(min = 2, max = 200, message = "Псевдонима трябва да е поне 2 символа")
    private String displayName;
}
