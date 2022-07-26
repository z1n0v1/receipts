package eu.zinovi.receipts.domain.model.binding.admin;

import eu.zinovi.receipts.domain.model.validation.RoleExists;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AdminRoleBindingModel {

    @NotNull(message = "Името на ролята е задължително")
    @RoleExists
    private String role;

    @NotEmpty(message = "Списъкът с правата е задължителен")
    private List<@Valid AdminCapabilityBindingModel> capabilities;
}
