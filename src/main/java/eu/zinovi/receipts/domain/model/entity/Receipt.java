package eu.zinovi.receipts.domain.model.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @ToString @AllArgsConstructor @Entity
@Table(name = "receipts", schema = "public")
public class Receipt extends BaseEntity {

    @Column(name = "date_of_purchase")
//    @Column(name = "date", nullable = false)
    private LocalDateTime dateOfPurchase;

    @Column(name = "total")
//    @Column(name = "total", nullable = false)
    private BigDecimal total;

    @Column(name = "receipt_lines", nullable = false, columnDefinition = "text")
    private String receiptLines;

    @Column(name = "analyzed", nullable = false)
    private Boolean analyzed;

    @ManyToOne @ToString.Exclude
    private Company company;

    @ManyToOne @ToString.Exclude
    private Store store;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.REMOVE) @ToString.Exclude
    private List<Item> Items;

    @Column(name = "items_total")
//    @Column(name = "items_total", nullable = false)
    private BigDecimal itemsTotal;

    @ManyToOne @ToString.Exclude
    private User user;

    @OneToOne @ToString.Exclude
    private ReceiptImage receiptImage;

    public Receipt() {
        Items = new ArrayList<>();
    }
}
