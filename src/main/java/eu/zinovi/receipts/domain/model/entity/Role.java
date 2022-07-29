package eu.zinovi.receipts.domain.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

@Getter
@Setter
@ToString
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
    private Collection<Capability> capabilities;

    public Role() {
        capabilities = new HashSet<>();
    }

    public Role(String name) {
        this();
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Role role = (Role) o;
        return Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }
}
