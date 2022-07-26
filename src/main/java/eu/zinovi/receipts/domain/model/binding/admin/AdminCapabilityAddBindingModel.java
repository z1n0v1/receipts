package eu.zinovi.receipts.domain.model.binding.admin;

import eu.zinovi.receipts.domain.model.validation.CapabilityExists;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AdminCapabilityAddBindingModel {

    @NotBlank(message = "Правото не може да е празно")
    @CapabilityExists
    String name;
}
