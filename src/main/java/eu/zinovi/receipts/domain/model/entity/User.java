package eu.zinovi.receipts.domain.model.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

@Getter @Setter @ToString @RequiredArgsConstructor
//@Entity(name = "users", schema = "receipts")
@Entity
@Table(name = "users", schema = "public")
public class User extends BaseEntity {

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @Column(name = "picture")
    private String picture;

    @Column(name = "google_id")
    private String googleId;

    @Column(name = "password")
    private String password;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "email_login_disabled", nullable = false)
    private boolean emailLoginDisabled;

    @Column(name = "registered_on", nullable = false)
    private LocalDateTime registeredOn;

    @Column(name = "last_seen", nullable = false)
    private LocalDateTime lastSeen;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE) @ToString.Exclude
    private Collection<ReceiptImage> receiptImages = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE) @ToString.Exclude
    private Collection<Receipt> receipts = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    @ToString.Exclude
    private Collection<Role> roles = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
