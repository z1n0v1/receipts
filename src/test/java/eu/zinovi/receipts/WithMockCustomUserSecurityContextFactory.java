package eu.zinovi.receipts;

import eu.zinovi.receipts.domain.user.EmailUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.stream.Collectors;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockEmailUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockEmailUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        EmailUser principal =
                new EmailUser(
                        customUser.displayName(),
                        customUser.picture(),
                        customUser.email(),
                        customUser.password(),
                        customUser.emailVerified(),
                        customUser.enabled(),
                        Arrays.stream(customUser.roles())
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList()));

        Authentication auth =
                new UsernamePasswordAuthenticationToken(principal, "password", principal.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
