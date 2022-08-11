package eu.zinovi.receipts.service.impl;

import eu.zinovi.receipts.domain.model.entity.Capability;
import eu.zinovi.receipts.domain.model.entity.Role;
import eu.zinovi.receipts.domain.model.entity.User;
import eu.zinovi.receipts.repository.RoleRepository;
import eu.zinovi.receipts.repository.UserRepository;
import eu.zinovi.receipts.domain.user.EmailUser;
import eu.zinovi.receipts.domain.user.GoogleOAuth2User;
import eu.zinovi.receipts.service.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static eu.zinovi.receipts.util.constants.MessageConstants.NOT_FOUND_USER_BY_EMAIL;
import static eu.zinovi.receipts.util.constants.MessageConstants.NO_PERMISSION_EMAIL_LOGIN;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public GoogleOAuth2User googleOAuth2LoginOrRegister(OidcUser oidcUser) {
        GoogleOAuth2User googleOAuth2User = new GoogleOAuth2User();

        User user = userRepository.getByEmail(oidcUser.getEmail()).orElse(null);

        if (user == null) {
            user = new User();
            user.setEmail(oidcUser.getEmail());
            user.setFirstName(oidcUser.getGivenName());
            user.setLastName(oidcUser.getFamilyName());
            user.setPicture(oidcUser.getPicture());
            user.setGoogleId(oidcUser.getAttribute("sub"));
            user.setEmailLoginDisabled(true);
            user.setEnabled(true);
            user.setEmailVerified(true);
            user.setRegisteredOn(LocalDateTime.now());
            user.setLastSeen(LocalDateTime.now());
            user.setDisplayName(oidcUser.getGivenName());
            user.getRoles().add(roleRepository.getByName("USER").orElse(null));
            //TODO add roles
            userRepository.saveAndFlush(user);
        } else {
            user.setGoogleId(oidcUser.getAttribute("sub"));
            user.setEmailVerified(true);
            if (user.getPicture() == null || user.getPicture().isEmpty()
            || user.getPicture().equals("/images/blank-avatar.png")) {
                user.setPicture(oidcUser.getPicture());
            }
            user.setLastSeen(LocalDateTime.now());
            userRepository.saveAndFlush(user);
        }

        googleOAuth2User.setEmail(user.getEmail());
        googleOAuth2User.setPicture(user.getPicture());
        googleOAuth2User.getAuthorities().addAll(getAuthorities(user.getRoles()));
        googleOAuth2User.setDisplayName(user.getDisplayName());
        googleOAuth2User.setIdToken(oidcUser.getIdToken());
        googleOAuth2User.getClaims().putAll(oidcUser.getClaims());

        return googleOAuth2User;
    }

    @Override
    @Transactional
    public EmailUser loadUserByUsername(String email) {
        User user = userRepository.getByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
                String.format(NOT_FOUND_USER_BY_EMAIL, email)));

        if (user.isEmailLoginDisabled()) {
            throw new UsernameNotFoundException(NO_PERMISSION_EMAIL_LOGIN);
        }

        return new EmailUser(
                user.getDisplayName(),
                user.getPicture(),
                user.getEmail(),
                user.getPassword(),
                user.isEmailVerified(),
                user.isEnabled(),
                getAuthorities(user.getRoles())
        );
    }

    private Collection<GrantedAuthority> getAuthorities(Collection<Role> roles) {
        return getGrantedAuthorities(getCapabilities(roles));
    }

    private SortedSet<String> getCapabilities(Collection<Role> roles) {

        // Ensure array iteration order is predictable (as per
        // UserDetails.getAuthorities() contract and SEC-717)
        // But does it make sense ? How do we implement this cleanly ?
        SortedSet<String> capabilities = new TreeSet<>();
        Set<Capability> collection = new HashSet<>();
        for (Role role : roles) {
            capabilities.add(role.getName());
            collection.addAll(role.getCapabilities());
        }
        for (Capability item : collection) {
            capabilities.add(item.getName().name());
        }
        return capabilities;
    }

    private Set<GrantedAuthority> getGrantedAuthorities(Collection<String> capabilities) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (String capability : capabilities) {
            authorities.add(new SimpleGrantedAuthority(capability));
        }
        return authorities;
    }

    @Override
    public void loginSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        User user = userRepository.getByEmail(authentication.getName()).orElse(null);
        if (user != null) {
            user.setLastSeen(LocalDateTime.now());
            userRepository.saveAndFlush(user);
        }

        httpServletResponse.sendRedirect("/home");
    }
}
