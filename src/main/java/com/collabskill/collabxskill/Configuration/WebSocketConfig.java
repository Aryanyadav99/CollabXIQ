package com.collabskill.collabxskill.Configuration;

import com.collabskill.collabxskill.security.JwtHandshakeInterceptor;
import com.collabskill.collabxskill.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtUtil jwtUtil;

    @Value("${app.websocket.url}")
    private String url;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat").setAllowedOriginPatterns(url) // for allowing cross-origin requests from the frontend
                .addInterceptors(new JwtHandshakeInterceptor(jwtUtil))  // for security checking during handshake
                .withSockJS(); // for older browsers that don't support WebSockets it try to solve using sse
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); // for broadcasting messages to clients TODO: convert to queue when i learn more about websocket
        registry.setApplicationDestinationPrefixes("/app"); // for messages sent from clients to server
    }
}
