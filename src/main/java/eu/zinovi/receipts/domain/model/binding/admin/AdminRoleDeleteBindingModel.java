package eu.zinovi.receipts.domain.model.binding.admin;

import eu.zinovi.receipts.domain.model.validation.RoleExists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data @NoArgsConstructor @AllArgsConstructor
public class AdminRoleDeleteBindingModel {

    @NotBlank(message = "Ролята не може да е празна")
    @RoleExists
    private String name;

}
