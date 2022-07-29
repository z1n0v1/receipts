package eu.zinovi.receipts.domain.model.entity;

import eu.zinovi.receipts.domain.model.enums.CapabilityEnum;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Getter @Setter @ToString @Entity
@Table(name = "capabilities", schema = "public")
public class Capability extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @ToString.Exclude
    @Column(name = "name", nullable = false, unique = true)
    private CapabilityEnum name;

    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "capabilities")
    @ToString.Exclude
    private Collection<Role> roles;

    public Capability() {
        roles = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Capability that = (Capability) o;
        return name == that.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }
}
