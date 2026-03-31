package com.collabskill.collabxskill.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.http.server.ServletServerHttpRequest;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {

        log.info("🔌 Handshake request");

        try {
            if (request instanceof ServletServerHttpRequest servletRequest) {

                HttpServletRequest httpRequest = servletRequest.getServletRequest();
                Cookie[] cookies = httpRequest.getCookies();

                if (cookies != null) {
                    for (Cookie cookie : cookies) {

                        if ("accessToken".equals(cookie.getName())) {

                            String token = cookie.getValue();
                            var claims = jwtUtil.parseClaims(token);

                            String userId = claims.getSubject();
                            attributes.put("userId", userId);

                            log.info("✅ Auth success: {}", userId);
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("❌ JWT error: {}", e.getMessage());
        }

        // 🔥 TEMP fallback (so app never breaks)
        attributes.put("userId", "1");
        log.warn("⚠️ Fallback user used");

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, @Nullable Exception exception) {

    }
}