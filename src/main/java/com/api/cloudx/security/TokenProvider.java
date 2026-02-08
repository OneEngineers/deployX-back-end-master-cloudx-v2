package com.api.cloudx.security;

import com.api.cloudx.config.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class TokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private final AppProperties appProperties;

    public TokenProvider(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    // Helper to generate a proper SecretKey from your string secret
    private SecretKey getSigningKey() {
        byte[] keyBytes = appProperties.getAuth().getTokenSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + appProperties.getAuth().getTokenExpirationMsec());

        return Jwts.builder()
                .subject(Long.toString(userPrincipal.getId())) // Removed 'set' prefix
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(getSigningKey()) // Algorithm HS512 is now inferred from key size
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey()) // Replaces setSigningKey
                .build()                     // Must call build() first
                .parseSignedClaims(token)    // Replaces parseClaimsJws
                .getPayload();               // Replaces getBody

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // JJWT 0.12 simplifies exception handling under JwtException
            logger.error("Invalid JWT token: {}", ex.getMessage());
        }
        return false;
    }
}