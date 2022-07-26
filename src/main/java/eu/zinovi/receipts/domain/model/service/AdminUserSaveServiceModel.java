package eu.zinovi.receipts.domain.model.service;

import lombok.Data;

import java.util.List;

@Data
public class AdminUserSaveServiceModel {
    private String firstName;
    private String lastName;
    private String displayName;

    private String email;

    private Boolean enabled;
    private Boolean emailLoginDisabled;

    private List<AdminUserRoleServiceModel> roles;
}
