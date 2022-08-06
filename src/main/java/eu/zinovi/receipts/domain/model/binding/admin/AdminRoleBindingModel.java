package eu.zinovi.receipts.domain.model.binding.admin;

import eu.zinovi.receipts.domain.model.validation.RoleExists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;

import static eu.zinovi.receipts.util.constants.MessageConstants.REQUIRED_CAPABILITY_LIST;
import static eu.zinovi.receipts.util.constants.MessageConstants.REQUIRED_ROLE_NAME;

@Data @NoArgsConstructor @AllArgsConstructor
public class AdminRoleBindingModel {

    @NotNull(message = REQUIRED_ROLE_NAME)
    @RoleExists
    private String role;

    @NotEmpty(message = REQUIRED_CAPABILITY_LIST)
    private Collection<@Valid AdminCapabilityBindingModel> capabilities;
}
