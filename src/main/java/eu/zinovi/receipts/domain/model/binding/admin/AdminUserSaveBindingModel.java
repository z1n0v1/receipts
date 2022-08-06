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

import static eu.zinovi.receipts.util.constants.MessageConstants.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class AdminUserSaveBindingModel {

    @NotBlank(message = REQUIRED_NAME)
    private String firstName;

    @NotBlank(message = REQUIRED_LAST_NAME)
    private String lastName;

    @NotBlank(message = REQUIRED_DISPLAY_NAME)
    private String displayName;

    @Email(message = INVALID_EMAIL)
    @EmailExists(message = USER_EMAIL_DONT_EXIST)
    private String email;

    @NotNull(message = REQUIRED_IS_ACTIVE)
    private Boolean enabled;

    @NotNull(message = REQUIRED_IS_EMAIL_LOGIN_DISABLED)
    private Boolean emailLoginDisabled;

    @NotEmpty(message = REQUIRED_MINIMUM_ONE_ROLE)
    private Collection<@Valid AdminUserRoleBindingModel> roles;
}
