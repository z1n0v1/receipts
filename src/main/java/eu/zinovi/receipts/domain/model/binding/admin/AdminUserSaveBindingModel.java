package eu.zinovi.receipts.domain.model.binding.admin;

import eu.zinovi.receipts.domain.model.validation.EmailExists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Data @NoArgsConstructor @AllArgsConstructor
public class AdminUserSaveBindingModel {

    @NotBlank(message = "Името е задължително")
    private String firstName;

    @NotBlank(message = "Фамилията е задължителна")
    private String lastName;

    @NotBlank(message = "Псевдонима е задължителен")
    private String displayName;

    @Email(message = "Невалиден имейл")
    @EmailExists(message = "Няма потребител с такъв имейл")
    private String email;

    @NotNull(message = "Активиран е задължително поле")
    private Boolean enabled;

    @NotNull(message = "Разрешен/Забранен достъп с имейл е задължително поле")
    private Boolean emailLoginDisabled;

    @NotEmpty(message = "Потребителя трябва да има поне една роля")
    private Collection<@Valid AdminUserRoleBindingModel> roles;
}
