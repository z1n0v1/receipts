package eu.zinovi.receipts.domain.model.view.admin;

import lombok.Data;

import java.util.List;

@Data
public class AdminRoleView {
    private String role;
    private String description;
    private List<AdminCapabilityView> capabilities;
    private List<AdminRoleUserView> users;
}