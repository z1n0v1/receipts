package eu.zinovi.receipts.domain.model.view;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDetailsView {
    private String firstName;
    private String lastName;
    private String displayName;
    private String email;
    private String picture;
    private LocalDateTime registeredOn;
    private boolean emailLoginDisabled;
}
