package eu.zinovi.receipts.domain.model.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(name = "roles")
@Table(name = "roles", schema = "public")
public class Role extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "roles") @ToString.Exclude
    private Collection<User> users = new ArrayList<>();

    @ManyToMany @ToString.Exclude
    private Collection<Capability> capabilities = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Role role = (Role) o;
        return getId() != null && Objects.equals(getId(), role.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
