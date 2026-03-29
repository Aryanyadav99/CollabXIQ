package com.collabskill.collabxskill.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.http.server.ServletServerHttpRequest;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    private  final JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if(request instanceof ServletServerHttpRequest serverHttpRequest){
            HttpServletRequest httpRequest = serverHttpRequest.getServletRequest();
            Cookie []cookies=httpRequest.getCookies();
            if(cookies!=null){
                for(Cookie cookie:cookies){
                    if(cookie.getName().equals("accessToken")){
                        String token=cookie.getValue();
                        try {
                            var claims=jwtUtil.parseClaims(token);
                            String userId=claims.getSubject();
                            attributes.put("userId", userId); // Store userId in attributes for later use
                            return true; // Allow the handshake to proceed
                        }
                        catch (Exception e) {
                            log.error("Invalid JWT token in WebSocket handshake: {}", e);
                        }
                    }
                }
            }
        }
        return false; // Rejected
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, @Nullable Exception exception) {
        //Noting to do after handshake
    }
}
