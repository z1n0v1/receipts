package eu.zinovi.receipts.domain.model.service;

import lombok.Data;

import java.util.UUID;

@Data
public class AdminCategorySaveServiceModel {
    private UUID id;
    private String name;
    private String color;
}
