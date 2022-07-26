package eu.zinovi.receipts.service;

import com.google.gson.Gson;
import eu.zinovi.receipts.domain.model.view.StompMessageView;
import eu.zinovi.receipts.domain.user.EmailUser;
import eu.zinovi.receipts.domain.user.GoogleOAuth2User;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.logging.Logger;

@Service
public class MessagingService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final Gson gson;

    public MessagingService(SimpMessagingTemplate simpMessagingTemplate, Gson gson) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.gson = gson;
    }

    public void sendMessage(String... args) {
        String message = args[0];
        String type = args.length == 2 ? args[1] : "info";
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//        String userName = ((Principal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getName();
        String userName = null;

        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user instanceof EmailUser) {
            userName = ((EmailUser) user).getEmail();
        } else if (user instanceof GoogleOAuth2User) {
            userName = ((GoogleOAuth2User) user).getEmail();
        } else if (user instanceof Principal) {
            userName = ((Principal) user).getName();
        }

        if (userName != null) {
            StompMessageView stompMessageView = new StompMessageView(message, type);
//            System.out.println("MessagingService sendMessage - User name: " + userName);
//            simpMessagingTemplate.convertAndSendToUser(userName, "/topic/alert-messages", gson.toJson(message));
            simpMessagingTemplate.convertAndSendToUser(userName, "/topic/alert-messages",
                    gson.toJson(stompMessageView));
//            simpMessagingTemplate.convertAndSendToUser(userName, "/topic/alert-messages", gson.toJson(message));
        }
//        simpMessagingTemplate.convertAndSend("/topic/alert-messages", gson.toJson(message));
    }
}
