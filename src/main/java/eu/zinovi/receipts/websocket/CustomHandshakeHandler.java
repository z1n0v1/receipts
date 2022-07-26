package eu.zinovi.receipts.websocket;

import eu.zinovi.receipts.domain.user.EmailUser;
import eu.zinovi.receipts.domain.user.GoogleOAuth2User;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {
    // Custom class for storing principal
    @Override
    protected Principal determineUser(
            ServerHttpRequest request,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        // Generate principal with UUID as name
//        String userName = UUID.randomUUID().toString();
        String userName = null;

        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user instanceof EmailUser) {
            userName = ((EmailUser) user).getEmail();
        } else if (user instanceof GoogleOAuth2User) {
            userName = ((GoogleOAuth2User) user).getEmail();
        } else if (user instanceof Principal) {
            userName = ((Principal) user).getName();
        }

//        System.out.println("CustomHandshakeHandler determineUser - User name: " + userName);

        return new StompPrincipal(userName);
//        return (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
