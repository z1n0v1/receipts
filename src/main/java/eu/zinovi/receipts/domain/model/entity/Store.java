package eu.zinovi.receipts.domain.model.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "stores", schema = "public")
public class Store extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

//    @Column(name = "address", nullable = false)
    @ManyToOne @ToString.Exclude
    private Address address;

    @ManyToOne @ToString.Exclude
    private Company company;
}
