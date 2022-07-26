package eu.zinovi.receipts.controller;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import eu.zinovi.receipts.WithMockEmailUser;
import eu.zinovi.receipts.util.CloudStorage;
import eu.zinovi.receipts.util.ReceiptProcessApi;
import eu.zinovi.receipts.util.RegisterBGApi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.mail.internet.MimeMessage;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerVerificationEmailIT {

    @MockBean
    ReceiptProcessApi receiptProcessApi;

    @MockBean
    CloudStorage cloudStorage;

    @MockBean
    RegisterBGApi registerBGApi;

    @Value("${mail.host}")
    private String mailhost;

    @Value("${mail.port}")
    private int mailport;

    private GreenMail greenMail;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        greenMail = new GreenMail(new ServerSetup(mailport, mailhost, "smtp"));
        greenMail.start();
    }

    @AfterEach
    public void tearDown() {
        greenMail.stop();
    }

    @Test
    void testRegistration() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/user/register")
                        .param("email", "test@test")
                        .param("displayName", "displaytest")
                        .param("firstName", "firstName")
                        .param("lastName", "lastName")
                        .param("password", "testpass")
                        .param("confirmPassword", "testpass")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/login"));

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        Assertions.assertEquals(1, receivedMessages.length);

        MimeMessage welcomeMessage = receivedMessages[0];

        Assertions.assertTrue(welcomeMessage.getContent().toString().
                contains("Моля потвърдете вашата електронна поща"));
    }

    @Test
    @WithMockEmailUser(emailVerified = false)
    public void testVerifyEmailAgainGet() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/verify/email?again=true")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/verify/email"));
    }
}
