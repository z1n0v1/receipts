package eu.zinovi.receipts.domain.model.service;

import lombok.Data;

import java.util.List;

@Data
public class AdminRoleServiceModel {

    String role;
    List<AdminCapabilityServiceModel> capabilities;
}
