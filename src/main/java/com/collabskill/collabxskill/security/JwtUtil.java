package com.collabskill.collabxskill.security;

import com.collabskill.collabxskill.Entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${security.jwt.access-expiration-ms}")
    private long accessExp;

    @Value("${security.jwt.refresh-expiration-ms}")
    private long refreshExp;


    public String generateAccessToken(User user) {
        return Jwts.builder().setSubject(user.getId()).claim("email", user.getEmail())
                .claim("ver", user.getTokenVersion()).setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExp))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256).compact();
    }

    public String generateRefreshToken(User user, String tokenId) {
        return Jwts.builder().setSubject(user.getId()).claim("tid", tokenId).claim("ver", user.getTokenVersion())
                .setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + refreshExp))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256).compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secret.getBytes())).build().parseClaimsJws(token)
                .getBody();
    }
}
