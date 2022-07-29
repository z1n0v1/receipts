package eu.zinovi.receipts.domain.model.binding.admin;

import eu.zinovi.receipts.domain.model.validation.CapabilityExists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data @NoArgsConstructor @AllArgsConstructor
public class AdminCapabilityBindingModel {


    @NotNull(message = "Имената на правата са задължителни")
    @CapabilityExists
    private String capability;

    @NotNull(message = "Състоянието на правата е задължително")
    private Boolean active;
}
