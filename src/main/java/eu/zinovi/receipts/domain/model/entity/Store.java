package eu.zinovi.receipts.domain.model.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Store store = (Store) o;
        return Objects.equals(name, store.name) && Objects.equals(address, store.address) && Objects.equals(company, store.company);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, address, company);
    }
}
