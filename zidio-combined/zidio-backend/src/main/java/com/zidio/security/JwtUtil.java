package com.zidio.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtUtil {

    private final Key key;
    private final long expirationMs;

    public JwtUtil(@Value("${app.jwt.secret}") String secret,
                   @Value("${app.jwt.expiration-ms}") long expirationMs) {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT secret (app.jwt.secret) must be configured and non-empty");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    @PostConstruct
    private void validateKey() {
        try {
            String t = Jwts.builder().setSubject("test").setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 1000))
                    .signWith(key).compact();
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(t);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new IllegalStateException("JWT configuration invalid: " + ex.getMessage(), ex);
        }
    }

    public String generateToken(String subject, String role) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    public Jws<Claims> parseToken(String token) throws JwtException {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
}
