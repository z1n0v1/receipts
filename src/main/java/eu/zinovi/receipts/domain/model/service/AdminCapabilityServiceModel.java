package eu.zinovi.receipts.domain.model.service;

import eu.zinovi.receipts.domain.model.enums.CapabilityEnum;
import lombok.Data;

@Data
public class AdminCapabilityServiceModel {
    CapabilityEnum capability;
    Boolean active;
}
