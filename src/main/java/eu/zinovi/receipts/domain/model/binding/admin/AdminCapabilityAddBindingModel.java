package eu.zinovi.receipts.domain.model.binding.admin;

import eu.zinovi.receipts.domain.model.validation.CapabilityExists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

import static eu.zinovi.receipts.util.constants.MessageConstants.EMPTY_CAPABILITY;

@Data @NoArgsConstructor @AllArgsConstructor
public class AdminCapabilityAddBindingModel {

    @NotBlank(message = EMPTY_CAPABILITY)
    @CapabilityExists
    String name;
}
