package eu.zinovi.receipts.domain.model.binding.admin;

import eu.zinovi.receipts.domain.model.validation.CapabilityExists;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminCapabilityBindingModel {


    @NotNull(message = "Имената на правата са задължителни")
    @CapabilityExists
    private String capability;

    @NotNull(message = "Състоянието на правата е задължително")
    private Boolean active;
}
