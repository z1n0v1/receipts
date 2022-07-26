package eu.zinovi.receipts.domain.model.service;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ItemAddServiceModel {
    private UUID receiptId;
    private String category;
    private String name;
    private BigDecimal quantity;
    private BigDecimal price;
}
