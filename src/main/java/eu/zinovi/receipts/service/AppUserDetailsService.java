package eu.zinovi.receipts.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.transaction.Transactional;

public class AppUserDetailsService implements UserDetailsService {
    private final AuthService authService;

    public AppUserDetailsService(AuthService authService) {
        this.authService = authService;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        return authService.loadUserByUsername(email);
    }

}
