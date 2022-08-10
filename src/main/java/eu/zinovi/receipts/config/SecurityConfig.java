package eu.zinovi.receipts.config;

import eu.zinovi.receipts.service.impl.ReceiptsUserDetailsService;
import eu.zinovi.receipts.service.impl.AuthServiceImpl;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, AuthServiceImpl authServiceImpl) throws Exception {

        return http
                .authorizeRequests()
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                    .antMatchers("/", "/user/register", "/user/verify/email",
                            "/legal/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                .formLogin()
                    .loginPage("/user/login").permitAll()
                    .successHandler(authServiceImpl::loginSuccess)
                    .and()
                .oauth2Login()
                    .userInfoEndpoint(userInfoEndpointConfig ->
                        userInfoEndpointConfig.oidcUserService(this.oidcUserService(authServiceImpl)))
                    .and()
                .logout()
                    .logoutUrl("/user/logout")
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .and()
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService(AuthServiceImpl authServiceImpl) {
        return new ReceiptsUserDetailsService(authServiceImpl);
    }

    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService(AuthServiceImpl authServiceImpl) {
        final OidcUserService delegate = new OidcUserService();

        return (userRequest) -> {
            // Delegate to the default implementation for loading a user
            OidcUser oidcUser = delegate.loadUser(userRequest);

            return authServiceImpl.googleOAuth2LoginOrRegister(oidcUser);

        };
    }
}
