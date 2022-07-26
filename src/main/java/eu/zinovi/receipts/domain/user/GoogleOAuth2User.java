package eu.zinovi.receipts.domain.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

@Getter @Setter
public class GoogleOAuth2User implements OidcUser {
    private Collection<GrantedAuthority> authorities;
    private Map<String, Object> attributes;
    private String email;
    private String displayName;
    private String picture;
    private OidcIdToken idToken;
    private OidcUserInfo userInfo;

    @Override
    public String toString() {
        return "GoogleOAuth2User{} " + super.toString() +
                " email: " + email +
                " displayName: " + displayName +
                " idToken: " + idToken +
                " userInfo: " + userInfo +
                attributes.toString() +
                authorities.toString();

    }

    public GoogleOAuth2User() {
        this.authorities = new HashSet<>();
        this.attributes = new HashMap<>();
    }

    @Override
    public String getName() {
        return this.displayName;
    }

    @Override
    public Map<String, Object> getClaims() {
        return this.attributes;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return OidcUserInfo.builder().build();
    }

    @Override
    public OidcIdToken getIdToken() {
        return this.idToken;
    }
}
