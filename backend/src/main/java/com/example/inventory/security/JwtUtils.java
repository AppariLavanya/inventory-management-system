package com.example.inventory.security;

import com.example.inventory.model.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Base64;

@Component
public class JwtUtils {

    private final Key key;
    private final long expirationMs;

    /**
     * Accepts either a plain secret string (will be UTF-8 bytes) or a base64-encoded secret.
     * Ensures key length is sufficient for HMAC-SHA algorithms.
     */
    public JwtUtils(@Value("${jwt.secret}") String secret,
                    @Value("${jwt.expirationMs}") long expirationMs) {
        byte[] keyBytes;

        // If the secret looks like base64, try decode — this offers flexibility for deployments.
        try {
            // crude heuristic: if contains whitespace or not valid base64, this may throw
            keyBytes = Base64.getDecoder().decode(secret);
            // If we decode then but decoded bytes are too short, fall back to bytes of secret
            if (keyBytes.length < 32) {
                keyBytes = secret.getBytes();
            }
        } catch (IllegalArgumentException ex) {
            keyBytes = secret.getBytes();
        }

        // Ensure key is at least 32 bytes for HS256
        if (keyBytes.length < 32) {
            // pad deterministically (not ideal for production secrets, but avoids runtime errors in dev)
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, Math.min(keyBytes.length, 32));
            for (int i = keyBytes.length; i < 32; i++) padded[i] = (byte) (i * 31);
            keyBytes = padded;
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    public String generateToken(String email, Long userId, Set<Role> roles) {

        // store roles as simple names without ROLE_ prefix (frontend-friendly)
        Set<String> simpleRoles = roles == null ? Set.of() : roles.stream()
                .map(r -> r.name().replaceFirst("^ROLE_", ""))
                .collect(Collectors.toSet());

        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(email)
                .claim("uid", userId)
                .claim("roles", simpleRoles)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public Long getUserIdFromToken(String token) {
        Object obj = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().get("uid");

        if (obj instanceof Number) return ((Number) obj).longValue();
        if (obj instanceof String) {
            try { return Long.parseLong((String) obj); } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public Set<String> getRolesFromToken(String token) {
        Object obj = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().get("roles");

        if (obj instanceof Collection<?> col) {
            return col.stream()
                    .map(r -> "ROLE_" + r.toString())
                    .collect(Collectors.toSet());
        }
        return Set.of();
    }
}






