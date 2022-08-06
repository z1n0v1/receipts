package eu.zinovi.receipts.domain.model.binding.admin;

import eu.zinovi.receipts.domain.model.validation.CapabilityExists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

import static eu.zinovi.receipts.util.constants.MessageConstants.REQUIRED_CAPABILITY_IS_ACTIVE;
import static eu.zinovi.receipts.util.constants.MessageConstants.REQUIRED_CAPABILITY_NAME;

@Data @NoArgsConstructor @AllArgsConstructor
public class AdminCapabilityBindingModel {


    @NotNull(message = REQUIRED_CAPABILITY_NAME)
    @CapabilityExists
    private String capability;

    @NotNull(message = REQUIRED_CAPABILITY_IS_ACTIVE)
    private Boolean active;
}
