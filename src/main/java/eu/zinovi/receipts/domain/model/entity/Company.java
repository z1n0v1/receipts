package eu.zinovi.receipts.domain.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Company company = (Company) o;
        return Objects.equals(eik, company.eik);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), eik);
    }
}
