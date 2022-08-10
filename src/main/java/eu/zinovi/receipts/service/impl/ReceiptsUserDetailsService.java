package eu.zinovi.receipts.service.impl;

import eu.zinovi.receipts.service.AuthService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.transaction.Transactional;

public class ReceiptsUserDetailsService implements UserDetailsService {
    private final AuthService authService;

    public ReceiptsUserDetailsService(AuthService authService) {
        this.authService = authService;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        return authService.loadUserByUsername(email);
    }

}
