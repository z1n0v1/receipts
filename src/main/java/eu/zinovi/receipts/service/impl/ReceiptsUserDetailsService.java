package eu.zinovi.receipts.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.transaction.Transactional;

public class ReceiptsUserDetailsService implements UserDetailsService {
    private final AuthServiceImpl authServiceImpl;

    public ReceiptsUserDetailsService(AuthServiceImpl authServiceImpl) {
        this.authServiceImpl = authServiceImpl;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        return authServiceImpl.loadUserByUsername(email);
    }

}
