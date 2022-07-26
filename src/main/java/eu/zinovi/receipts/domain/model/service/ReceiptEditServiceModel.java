package eu.zinovi.receipts.domain.model.service;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReceiptEditServiceModel {
    private UUID receiptId;
    private String companyEik;
    private String storeName;
    private String storeAddress;
    private LocalDateTime receiptDate;
    private BigDecimal total;
}
