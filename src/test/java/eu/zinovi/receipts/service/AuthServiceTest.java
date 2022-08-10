package eu.zinovi.receipts.service;

import eu.zinovi.receipts.domain.model.entity.Capability;
import eu.zinovi.receipts.domain.model.entity.Role;
import eu.zinovi.receipts.domain.model.entity.User;
import eu.zinovi.receipts.domain.model.enums.CapabilityEnum;
import eu.zinovi.receipts.domain.user.GoogleOAuth2User;
import eu.zinovi.receipts.repository.RoleRepository;
import eu.zinovi.receipts.repository.UserRepository;
import eu.zinovi.receipts.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    private AuthService authService;
    private User user;
    private Role role;

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Captor
    ArgumentCaptor<User> userArgumentCaptor;

    @BeforeEach
    public void setUp() {
        Capability capViewHome = new Capability();
        capViewHome.setName(CapabilityEnum.CAP_VIEW_HOME);
        capViewHome.setDescription("View home page");
        Capability capViewReceipts = new Capability();
        capViewReceipts.setName(CapabilityEnum.CAP_VIEW_RECEIPT);
        capViewReceipts.setDescription("View receipts");

        role = new Role();
        role.setName("USER");
        role.setDescription("User");
        role.setCapabilities(Set.of(capViewHome, capViewReceipts));

        this.user = new User();
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setDisplayName("displayName");
        user.setEmail("email");
        user.setEmailVerified(true);
        user.setPicture("picture");
        user.setGoogleId("googleId");
        user.setPassword("password");
        user.setEnabled(true);
        user.setEmailLoginDisabled(false);
        user.setRegisteredOn(LocalDateTime.of(2020, 1, 1, 0, 0));
        user.setLastSeen(LocalDateTime.of(2020, 1, 1, 0, 0));
        user.setRoles(Set.of(role));

        authService = new AuthServiceImpl(userRepository, roleRepository);
    }

    @Test
    public void testGoogleOAuth2LoginOrRegisterWithExistingUser() {
        when(userRepository.getByEmail("email")).thenReturn(Optional.of(user));
//        when(roleRepository.getByName("USER")).thenReturn(Optional.of(role));

        GoogleOAuth2User oAuth2User = new GoogleOAuth2User();
        oAuth2User.setEmail("email");
        oAuth2User.setPicture("picture");

        GoogleOAuth2User googleOAuth2User = authService.googleOAuth2LoginOrRegister(oAuth2User);
        Assertions.assertEquals(user.getDisplayName(), googleOAuth2User.getDisplayName());
    }

    @Test
    public void testGoogleOAuth2LoginOrRegisterWithNewUser() {
//        when(userRepository.getByEmail("email")).thenReturn(Optional.of(user));
        when(roleRepository.getByName("USER")).thenReturn(Optional.of(role));

        GoogleOAuth2User oAuth2User = new GoogleOAuth2User();
        oAuth2User.setEmail("email");
        oAuth2User.setPicture("picture");
        Set<GrantedAuthority> caps = new HashSet<>() {{
            add(new SimpleGrantedAuthority("CAP_VIEW_HOME"));
            add(new SimpleGrantedAuthority("CAP_VIEW_RECEIPT"));
        }};
        oAuth2User.setAuthorities(caps);

        authService.googleOAuth2LoginOrRegister(oAuth2User);
        Mockito.verify(userRepository).saveAndFlush(userArgumentCaptor.capture());

        User user = userArgumentCaptor.getValue();
        Assertions.assertEquals(oAuth2User.getEmail(), user.getEmail());
    }
}
