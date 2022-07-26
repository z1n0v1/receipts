package eu.zinovi.receipts.domain.model.view.admin;

import lombok.Builder;
import lombok.Data;

@Data
//@Builder
public class AdminCapabilityView {
    private String capability;
    private String description;
    private boolean active;
}
