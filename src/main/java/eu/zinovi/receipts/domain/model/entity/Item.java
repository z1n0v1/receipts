package eu.zinovi.receipts.domain.model.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString @Entity
@Table(name = "items", schema = "public")
public class Item extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "quantity", nullable = false, columnDefinition = "decimal(19,3)")
    private BigDecimal quantity;

    @Column(name = "position", nullable = false)
    private Integer position;

    @ManyToOne
    private Category category;

    @ManyToOne @ToString.Exclude
    Receipt receipt;
}
