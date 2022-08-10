package eu.zinovi.receipts.service.impl;

import com.google.gson.Gson;
import eu.zinovi.receipts.domain.model.view.StompMessageView;
import eu.zinovi.receipts.domain.user.EmailUser;
import eu.zinovi.receipts.domain.user.GoogleOAuth2User;
import eu.zinovi.receipts.service.MessagingService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class MessagingServiceImpl implements MessagingService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final Gson gson;

    public MessagingServiceImpl(SimpMessagingTemplate simpMessagingTemplate, Gson gson) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.gson = gson;
    }

    @Override
    public void sendMessage(String... args) {
        String message = args[0];
        String type = args.length == 2 ? args[1] : "info";

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
            simpMessagingTemplate.convertAndSendToUser(userName, "/topic/alert-messages",
                    gson.toJson(stompMessageView));
        }
    }
}
