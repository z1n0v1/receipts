package eu.zinovi.receipts.service.impl;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import eu.zinovi.receipts.domain.exception.EmailVerificationException;
import eu.zinovi.receipts.service.EmailVerificationService;
import eu.zinovi.receipts.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.IOException;

import static eu.zinovi.receipts.util.constants.EmailConstants.*;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final VerificationTokenService verificationTokenService;

    @Value("${receipts.email.from}")
    private String emailFrom;

    @Value("${receipts.baseUrl}")
    private String baseUrl;

    private final String emailProvider;

    private final SendGrid sendGrid;
    public EmailVerificationServiceImpl(
            VerificationTokenService verificationTokenService,
            @Value("${receipts.email.provider}") String emailProvider, SendGrid sendGrid) {
        this.verificationTokenService = verificationTokenService;
        this.emailProvider = emailProvider;
        this.sendGrid = sendGrid;
    }

    @Override
    public void sendVerificationEmail(String email) {
        if (emailProvider.equals("mailHog")) {
            sendLocalConfirmationCode(email, verificationTokenService.createVerificationToken(email));
        } else if (emailProvider.equals("sendGrid")) {
            sendGridConfirmationCode(email, verificationTokenService.createVerificationToken(email));
        } else {
            throw new EmailVerificationException("Unknown email provider: " + emailProvider);

        }
    }

    private void sendGridConfirmationCode(String email, String verificationToken) {
        Email from = new Email(this.emailFrom, EMAIL_FROM);
        Email to = new Email(email);

        Content content = new Content("text/html", getEmailBody(verificationToken));
        Mail mail = new Mail(from, EMAIL_SUBJECT, to, content);

        try {
            Request request = new Request();

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sendGrid.api(request);
        } catch (IOException ex) {
            throw new EmailVerificationException();
        }
    }

    private void sendLocalConfirmationCode(String email, String verificationToken) {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost("localhost");
        mailSender.setPort(1025);
        mailSender.setUsername("");
        mailSender.setPassword("");
        mailSender.setDefaultEncoding("UTF-8");
        mailSender.setProtocol("smtp");

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        try {
            helper.setText(getEmailBody(verificationToken), true); // Use this or above line.
            helper.setTo(email);
            helper.setSubject(EMAIL_SUBJECT);
            helper.setFrom(this.emailFrom, EMAIL_FROM);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new EmailVerificationException();
        }
    }

    private String getEmailBody(String verificationToken) {
        final String verificationUrl = this.baseUrl + "/user/verify/email?code=" + verificationToken;

        return String.format(EMAIL_BODY, verificationUrl, verificationUrl);
    }

}
