package eu.zinovi.receipts.service;

import eu.zinovi.receipts.domain.model.entity.Capability;
import eu.zinovi.receipts.domain.model.entity.Role;
import eu.zinovi.receipts.domain.model.entity.User;
import eu.zinovi.receipts.domain.model.entity.VerificationToken;
import eu.zinovi.receipts.domain.model.enums.CapabilityEnum;
import eu.zinovi.receipts.repository.UserRepository;
import eu.zinovi.receipts.repository.VerificationTokenRepository;
import eu.zinovi.receipts.service.impl.VerificationTokenServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VerificationTokenServiceTest {

    private VerificationTokenService verificationTokenService;
    private VerificationToken verificationToken;
    private User user;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    CacheManager cacheManager;

    @BeforeEach
    public void setUp() {
        verificationTokenService = new VerificationTokenServiceImpl(verificationTokenRepository, userRepository, cacheManager);

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

        verificationToken = new VerificationToken();
        verificationToken.setUser(user);
        verificationToken.setToken("token");
        verificationToken.setExpiryDate(LocalDateTime.now().plusDays(1));
    }

    @Test
    public void testCreateVerificationToken() {

        when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user);
        when(verificationTokenRepository.getByUserEmail(user.getEmail())).thenReturn(Optional.of(verificationToken));

        Assertions.assertDoesNotThrow( () ->
                UUID.fromString(verificationTokenService.createVerificationToken(user.getEmail())));

    }

    @Test
    public void testVerifyToken() {
        when(verificationTokenRepository.getByToken(verificationToken.getToken())).thenReturn(Optional.of(verificationToken));
//        when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user);
//        when(userRepository.save(user)).thenReturn(user);
        verificationTokenService.verifyToken(verificationToken.getToken());
    }
}
