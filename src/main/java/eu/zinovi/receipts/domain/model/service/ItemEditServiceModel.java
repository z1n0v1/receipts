package eu.zinovi.receipts.domain.model.service;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ItemEditServiceModel {
    private UUID receiptId;
    private Integer position;
    private String category;
    private String name;
    private BigDecimal quantity;
    private BigDecimal price;
}
