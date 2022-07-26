package eu.zinovi.receipts.domain.model.view;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class ReceiptListView {
    private UUID receiptId;
    private String imageUrl;
    private String companyName;
    private String storeName;
    private LocalDateTime addedOn;
    private BigDecimal total;
    private BigDecimal itemsTotal;
    private String categories;
//    private List<ItemView> items;
}
