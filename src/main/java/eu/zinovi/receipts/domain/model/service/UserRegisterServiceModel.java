package eu.zinovi.receipts.domain.model.service;

import lombok.Data;

@Data
public class UserRegisterServiceModel {
    private String firstName;
    private String lastName;
    private String displayName;
    private String email;
    private String password;
}
