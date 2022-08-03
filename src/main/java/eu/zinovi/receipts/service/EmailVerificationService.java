package eu.zinovi.receipts.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import eu.zinovi.receipts.domain.exception.EmailVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.IOException;

@Service
public class EmailVerificationService {

    private final VerificationTokenService verificationTokenService;

    @Value("${receipts.email.from}")
    private String emailFrom;

    @Value("${receipts.baseUrl}")
    private String baseUrl;

    @Value("${receipts.email.provider}")
    private String emailProvider;

    @Value("${receipts.email.provider.sendgrid.api-key}")
    private String sendGridKey;


    public EmailVerificationService(VerificationTokenService verificationTokenService) {
        this.verificationTokenService = verificationTokenService;
    }

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
        Email from = new Email(this.emailFrom, "Бележки.бг");
        String subject = "Потвърдете вашата електронна поща";
        Email to = new Email(email);

        Content content = new Content("text/html", getEmailBody(verificationToken));
        Mail mail = new Mail(from, subject, to, content);

        try {
            SendGrid sg = new SendGrid(this.sendGridKey);
            Request request = new Request();

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
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
            helper.setSubject("Потвърдете вашата електронна поща");
            helper.setFrom(this.emailFrom);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new EmailVerificationException();
        }
    }

    private String getEmailBody(String verificationToken) {
        return  "<p> Моля потвърдете вашата електронна поща </p>" +
                "<p> <a href=\"" + this.baseUrl + "/user/verify/email?code=" + verificationToken + "\">" +
                this.baseUrl + "/user/email/verify?code=" + verificationToken + "</a></p>";
    }

}
