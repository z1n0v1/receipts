package eu.zinovi.receipts.service;

import eu.zinovi.receipts.domain.model.entity.Capability;
import eu.zinovi.receipts.domain.model.entity.Role;
import eu.zinovi.receipts.domain.model.entity.User;
import eu.zinovi.receipts.domain.model.enums.CapabilityEnum;
import eu.zinovi.receipts.domain.user.EmailUser;
import eu.zinovi.receipts.repository.RoleRepository;
import eu.zinovi.receipts.repository.UserRepository;
import eu.zinovi.receipts.service.impl.AuthServiceImpl;
import eu.zinovi.receipts.service.impl.ReceiptsUserDetailsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReceiptsUserDetailsServiceTest {

    private ReceiptsUserDetailsService toTest;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @BeforeEach
    public void setUp() {
        AuthService authService = new AuthServiceImpl(userRepository, roleRepository);
        toTest = new ReceiptsUserDetailsService(authService);
    }

    @Test
    public void loadUserByUsername_UserExists() {

        // arrange

//        EmailUser user = new EmailUser(
//                "displayName",
//                "picture",
//                "email",
//                "password",
//                true,
//                true,
//                Set.of(new SimpleGrantedAuthority("CAP_VIEW_HOME"),
//                        new SimpleGrantedAuthority("CAP_VIEW_RECEIPTS")));

        Capability capViewHome = new Capability();
        capViewHome.setName(CapabilityEnum.CAP_VIEW_HOME);
        capViewHome.setDescription("View home page");
        Capability capViewReceipts = new Capability();
        capViewReceipts.setName(CapabilityEnum.CAP_VIEW_RECEIPT);
        capViewReceipts.setDescription("View receipts");

        Role role = new Role();
        role.setName("USER");
        role.setDescription("User");
        role.setCapabilities(Set.of(capViewHome, capViewReceipts));

        User user = new User();
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

        when(userRepository.getByEmail("email")).thenReturn(Optional.of(user));

        // act

        EmailUser emailUser = (EmailUser) toTest.loadUserByUsername(user.getEmail());

        // assert

        Assertions.assertEquals(user.getDisplayName(), emailUser.getDisplayName());
        Assertions.assertEquals(user.getPicture(), emailUser.getPicture());
        Assertions.assertEquals(emailUser.getEmail(), user.getEmail());
        Assertions.assertEquals(emailUser.getPassword(), user.getPassword());
        Assertions.assertEquals(emailUser.isEmailVerified(), user.isEmailVerified());
        Assertions.assertEquals(emailUser.isEnabled(), user.isEnabled());

        var authorities = emailUser.getAuthorities();

        Assertions.assertEquals(3, authorities.size());
        Assertions.assertTrue(authorities.contains(new SimpleGrantedAuthority("CAP_VIEW_HOME")));
        Assertions.assertTrue(authorities.contains(new SimpleGrantedAuthority("CAP_VIEW_RECEIPT")));

    }

    @Test
    public void loadUserByUsername_UserDoesNotExist() {

        // arrange

        when(userRepository.getByEmail("email")).thenReturn(Optional.empty());

        // act & assert

        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> toTest.loadUserByUsername("email"));
    }

    @Test
    public void loadUserByUsername_UserDoesNotExist_EmailLoginDisabled() {

        // arrange

        User user = new User();
        user.setEmailLoginDisabled(true);

        when(userRepository.getByEmail("email")).thenReturn(Optional.of(user));

        // act & assert

        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> toTest.loadUserByUsername("email"));
    }


}
