package eu.zinovi.receipts.domain.model.binding.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Collection;

import static eu.zinovi.receipts.util.constants.MessageConstants.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class AdminRoleAddBindingModel {
    @NotBlank(message = REQUIRED_ROLE_NAME)
    @Size(min = 3, max = 30, message = REQUIRED_ROLE_NAME_MINIMUM_LENGTH)
    private String name;

    @NotEmpty(message = REQUIRED_ROLE_NUM_CAPABILITIES)
    private Collection<@Valid AdminCapabilityAddBindingModel> capabilities;
}
