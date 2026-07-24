package com.higuitar.medicare.security;



import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Creates and validates JWT tokens signed with HMAC-SHA (HS256).
 * <p>
 * The token subject is the user's email. The signing key and lifetime are
 * externalized in the {@code jwt.secret} (Base64-encoded) and
 * {@code jwt.expiration} (milliseconds) properties.
 */
@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long expirationMs;

    /**
     * Generates a signed token for the given user, valid for
     * {@code jwt.expiration} milliseconds from now.
     *
     * @param userDetails the authenticated user
     * @return the compact, signed JWT
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }
    /**
     * Extracts the subject (user email) from a signed token.
     *
     * @param token the JWT to parse
     * @return the email stored as the token subject
     * @throws JwtException if the token is malformed, expired or has an invalid signature
     */
    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    /**
     * Checks that the token signature is valid and that its subject matches
     * the given user's email.
     *
     * @param token       the JWT to validate
     * @param userDetails the user to match against
     * @return {@code true} when the token is valid for the user; {@code false}
     * instead of throwing when the token is invalid
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String email = extractEmail(token);
            return email.equals(userDetails.getUsername());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
