package eu.zinovi.receipts.domain.model.entity;

import eu.zinovi.receipts.domain.model.enums.CapabilityEnum;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Getter @Setter @ToString @NoArgsConstructor @Entity
@Table(name = "capabilities", schema = "public")
public class Capability extends BaseEntity {

    @Enumerated(EnumType.STRING) @ToString.Exclude
    @Column(name = "name", nullable = false, unique = true)
    private CapabilityEnum name;

    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "capabilities") @ToString.Exclude
    private Collection<Role> roles = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Capability capability = (Capability) o;
        return getId() != null && Objects.equals(getId(), capability.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}