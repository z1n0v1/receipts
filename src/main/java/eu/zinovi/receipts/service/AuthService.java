package eu.zinovi.receipts.service;

import eu.zinovi.receipts.domain.user.EmailUser;
import eu.zinovi.receipts.domain.user.GoogleOAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;

public interface AuthService {
    @Transactional
    GoogleOAuth2User googleOAuth2LoginOrRegister(OidcUser oidcUser);

    @Transactional
    EmailUser loadUserByUsername(String email);

    void loginSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException;
}
