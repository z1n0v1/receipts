package eu.zinovi.receipts.domain.user;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

public class EmailUser implements UserDetails, CredentialsContainer {
//    private UUID id;

//   private String firstName;
//    private String lastName;


    private String displayName;
    private final String picture;
    private final String email;
    private String password;
    private final boolean emailVerified;
    private final boolean enabled;
    private final Collection<GrantedAuthority> authorities;

    public EmailUser(String displayName, String picture, String email, String password, boolean emailVerified,
                     boolean enabled, Collection<GrantedAuthority> authorities) {
        this.displayName = displayName;
        this.picture = picture;
        this.email = email;
        this.password = password;
        this.emailVerified = emailVerified;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public String getPicture() {
        return picture;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String toString() {
        return getClass().getName() + " [" +
                "Email=" + this.email + ", " +
                "EmailVerified=" + this.emailVerified + ", " +
                "Enabled=" + this.enabled + ", " +
                "DisplayName=" + this.displayName + ", " +
                "Granted Authorities=" + this.authorities + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailUser that = (EmailUser) o;
        return email.equals(that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.enabled;
//        return this.enabled && this.emailVerified;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.enabled;
//        return this.enabled && this.emailVerified;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.enabled;
//        return this.enabled && this.emailVerified;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
//        return this.enabled && this.emailVerified;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }
}
