package eu.zinovi.receipts.service;

import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import eu.zinovi.receipts.domain.exception.EmailVerificationException;
import eu.zinovi.receipts.domain.model.entity.Capability;
import eu.zinovi.receipts.domain.model.entity.Role;
import eu.zinovi.receipts.domain.model.entity.User;
import eu.zinovi.receipts.domain.model.entity.VerificationToken;
import eu.zinovi.receipts.domain.model.enums.CapabilityEnum;
import eu.zinovi.receipts.service.impl.EmailVerificationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailVerificationServiceTest {


    @Mock
    VerificationTokenService verificationTokenService;

    @Mock
    SendGrid sendGrid;

    private EmailVerificationService emailVerificationService;
    private VerificationToken verificationToken;
    private User user;

    @Captor
    ArgumentCaptor<Request> requestArgumentCaptor;

    @BeforeEach
    public void setUp() {
        emailVerificationService = new EmailVerificationServiceImpl(
                verificationTokenService, "sendGrid", sendGrid);

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

        verificationToken = new VerificationToken();
        verificationToken.setUser(user);
        verificationToken.setToken("token");
        verificationToken.setExpiryDate(LocalDateTime.now().plusDays(1));
    }

    @Test
    public void testSendVerificationNullEmailSendGrid() throws IOException {
        when(verificationTokenService.createVerificationToken(user.getEmail()))
                .thenReturn(verificationToken.getToken());

        emailVerificationService.sendVerificationEmail(user.getEmail());
        Mockito.verify(sendGrid).api(requestArgumentCaptor.capture());

        Request request = requestArgumentCaptor.getValue();
        Assertions.assertTrue(request.getBody().contains(verificationToken.getToken()));

    }

    @Test
    public void testSendVerificationNullEmailSendGridException() throws IOException {
        when(verificationTokenService.createVerificationToken(user.getEmail()))
                .thenReturn(verificationToken.getToken());

        doThrow(IOException.class).when(sendGrid).api(any());
        Assertions.assertThrows(EmailVerificationException.class,
                () -> emailVerificationService.sendVerificationEmail(user.getEmail()));
    }

}
