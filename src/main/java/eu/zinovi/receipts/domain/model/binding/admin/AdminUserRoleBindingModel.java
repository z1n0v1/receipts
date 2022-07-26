package eu.zinovi.receipts.domain.model.binding.admin;

import eu.zinovi.receipts.domain.model.validation.RoleExists;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminUserRoleBindingModel {

    @NotNull
    @RoleExists
    private String name;

    @NotNull
    private Boolean selected;
}
