package eu.zinovi.receipts.domain.model.binding.admin;

import eu.zinovi.receipts.domain.model.validation.RoleExists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

import static eu.zinovi.receipts.util.constants.MessageConstants.REQUIRED_ROLE_NAME;

@Data @NoArgsConstructor @AllArgsConstructor
public class AdminUserRoleBindingModel {

    @NotNull(message = REQUIRED_ROLE_NAME)
    @RoleExists
    private String name;

    @NotNull
    private Boolean selected;
}
