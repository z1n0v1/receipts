package eu.zinovi.receipts.domain.model.view;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemView {
    private Integer position;
    private String name;
    private BigDecimal quantity;
    private BigDecimal price;
    private String category;
}
