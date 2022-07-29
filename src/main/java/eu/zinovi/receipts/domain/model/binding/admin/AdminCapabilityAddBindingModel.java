package eu.zinovi.receipts.domain.model.binding.admin;

import eu.zinovi.receipts.domain.model.validation.CapabilityExists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data @NoArgsConstructor @AllArgsConstructor
public class AdminCapabilityAddBindingModel {

    @NotBlank(message = "Правото не може да е празно")
    @CapabilityExists
    String name;
}
