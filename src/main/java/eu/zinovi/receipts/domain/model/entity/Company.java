package eu.zinovi.receipts.domain.model.entity;

import lombok.*;

import javax.persistence.*;

@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor @Entity
@Table(name = "companies", schema = "public", indexes = {
        @Index(columnList = "eik", unique = true)
})
public class Company extends BaseEntity {

    @Column(name = "eik", nullable = false, unique = true)
    private String eik;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "activity", nullable = false, columnDefinition = "text")
    private String activity;

    @ManyToOne @ToString.Exclude
    private Address address;

}
