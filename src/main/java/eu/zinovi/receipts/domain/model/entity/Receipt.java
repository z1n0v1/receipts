package eu.zinovi.receipts.domain.model.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Getter @Setter @ToString @Entity
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
    private Collection<Item> Items;

    @Column(name = "items_total")
//    @Column(name = "items_total", nullable = false)
    private BigDecimal itemsTotal;

    @ManyToOne @ToString.Exclude
    private User user;

    @OneToOne @ToString.Exclude
    private ReceiptImage receiptImage;

    public Receipt() {
        Items = new HashSet<>();
    }

    public Receipt(LocalDateTime dateOfPurchase, BigDecimal total, String receiptLines, Boolean analyzed, Company company, Store store, List<Item> items, BigDecimal itemsTotal, User user, ReceiptImage receiptImage) {
        this();
        this.dateOfPurchase = dateOfPurchase;
        this.total = total;
        this.receiptLines = receiptLines;
        this.analyzed = analyzed;
        this.company = company;
        this.store = store;
        Items = items;
        this.itemsTotal = itemsTotal;
        this.user = user;
        this.receiptImage = receiptImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Receipt receipt = (Receipt) o;
        return Objects.equals(receiptLines, receipt.receiptLines);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), receiptLines);
    }
}
