package eu.zinovi.receipts.domain.model.view;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Getter @Setter
public class ReceiptDetailsView {
    private UUID id;
    private UUID imageId;
    private String imageUrl;
    private LocalDateTime date;
    private BigDecimal total;

    private String companyName;
    private String companyAddress;
    private Long companyEik;

    private String storeName;
    private String storeAddress;

    private Collection<ItemView> items;
    private BigDecimal itemsTotal;

    public ReceiptDetailsView() {
        items = new ArrayList<>();
    }

}
