package eu.zinovi.receipts.domain.model.view.admin;

import lombok.Data;

import java.util.List;

@Data
public class AdminUserView {
    private String firstName;
    private String lastName;
    private String displayName;
    private String picture;
    private String email;
    private boolean emailVerified;
    private boolean googleId;
    private boolean enabled;
    private boolean emailLoginDisabled;
    private String registeredOn;
    private String lastSeen;
    private List<AdminUserRoleView> roles;
}
