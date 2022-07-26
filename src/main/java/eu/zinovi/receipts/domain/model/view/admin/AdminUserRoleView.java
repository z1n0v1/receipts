package eu.zinovi.receipts.domain.model.view.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class AdminUserRoleView {
    private String name;
    private boolean selected;
}
