package eu.zinovi.receipts.domain.model.service;

import lombok.Data;

import java.util.Set;

@Data
public class AdminRoleAddServiceModel {
    private String name;
    private Set<AdminCapabilityAddServiceModel> capabilities;
}
