package eu.zinovi.receipts.domain.model.binding.admin;

import eu.zinovi.receipts.domain.model.validation.RoleExists;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AdminRoleDeleteBindingModel {

    @NotBlank(message = "Ролята не може да е празна")
    @RoleExists
    private String name;

}
