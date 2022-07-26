package eu.zinovi.receipts.domain.model.binding.user;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UserDetailsBindingModel {

    @NotNull(message = "Името е задължително")
    @Size(min = 2, max = 200, message = "Името трябва да е поне 2 символа")
    private String firstName;

    @NotNull(message = "Фамилията е задължителна")
    @Size(min = 2, max = 200, message = "Фамилията трябва да е поне 2 символа")
    private String lastName;

    @NotNull(message = "Псевдонима е задължителен")
    @Size(min = 2, max = 200, message = "Псевдонима трябва да е поне 2 символа")
    private String displayName;
}
