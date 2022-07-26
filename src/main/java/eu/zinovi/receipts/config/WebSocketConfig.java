package eu.zinovi.receipts.config;

import eu.zinovi.receipts.websocket.CustomHandshakeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
//        config.enableSimpleBroker("/secured/topic", "/secured/queue", "/secured/user");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // ...
        registry.addEndpoint("/alert-messages")
                .setHandshakeHandler(new CustomHandshakeHandler());
        registry.addEndpoint("/alert-messages")
                .setHandshakeHandler(new CustomHandshakeHandler())
                .withSockJS();
    }
}
