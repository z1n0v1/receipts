package eu.zinovi.receipts.domain.model.binding.admin;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class AdminRoleAddBindingModel {
    @NotBlank(message = "Ролята не може да е празна")
    @Size(min = 3, max = 30, message = "Ролята трябва да е между 3 и 30 символа")
    private String name;

    @NotEmpty(message = "Ролята трябва да има поне едно право")
    private Set<@Valid AdminCapabilityAddBindingModel> capabilities;
}
