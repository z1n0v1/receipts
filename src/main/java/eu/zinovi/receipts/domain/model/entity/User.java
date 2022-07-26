package eu.zinovi.receipts.domain.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

@Getter @Setter @ToString @Entity
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
    private Collection<ReceiptImage> receiptImages;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE) @ToString.Exclude
    private Collection<Receipt> receipts;

    public User() {
        receiptImages = new HashSet<>();
        receipts = new HashSet<>();
    }

    public User(String firstName, String lastName, String displayName, String email, boolean emailVerified,
                boolean enabled, boolean emailLoginDisabled, LocalDateTime registeredOn, LocalDateTime lastSeen) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.email = email;
        this.emailVerified = emailVerified;
        this.enabled = enabled;
        this.emailLoginDisabled = emailLoginDisabled;
        this.registeredOn = registeredOn;
        this.lastSeen = lastSeen;
    }

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
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), email);
    }
}
