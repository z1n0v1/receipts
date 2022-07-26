package eu.zinovi.receipts.domain.model.view.admin;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AdminReceiptView {

    private UUID imageId;
    String imageUrl;
    String receiptLines;

    private LocalDateTime date;
    private BigDecimal total;

    private Long companyEik;

    private String storeName;
    private String storeAddress;

    private BigDecimal itemsTotal;

}
