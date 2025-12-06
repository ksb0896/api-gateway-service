package com.ksb.micro.api_gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private final SecretKey key = Keys.hmacShaKeyFor("MySecureKeyForMicroservicesMustBeVeryLongAndSafe".getBytes());

    public boolean validateToken(String token) {
        try {
            logger.debug(">>> [JWT] Starting token validation...");
            Claims claims = getClaims(token);

            String subject = claims.getSubject();
            Date expiration = claims.getExpiration();
            Date now = new Date();

            logger.info(">>> [JWT] Token Claims - Subject: {}, Expiration: {}, Current Time: {}", subject, expiration, now);

            if (expiration != null && expiration.before(now)) {
                logger.warn(">>> [JWT] ✗ Token EXPIRED at: {}", expiration);
                return false;
            }

            logger.info(">>> [JWT] ✓ Token validation SUCCESSFUL");
            return true;
        } catch (io.jsonwebtoken.security.SignatureException e) {
            logger.error(">>> [JWT] ✗ Invalid JWT signature: {}", e.getMessage());
            return false;
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            logger.error(">>> [JWT] ✗ Invalid JWT format: {}", e.getMessage());
            return false;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            logger.error(">>> [JWT] ✗ JWT token expired: {}", e.getMessage());
            return false;
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            logger.error(">>> [JWT] ✗ Unsupported JWT token: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            logger.error(">>> [JWT] ✗ JWT claims string is empty: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error(">>> [JWT] ✗ Unexpected error during token validation: {}", e.getMessage(), e);
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            String username = getClaims(token).getSubject();
            logger.debug(">>> [JWT] Extracted username from token: {}", username);
            return username;
        } catch (Exception e) {
            logger.error(">>> [JWT] Failed to extract username from token: {}", e.getMessage());
            throw new RuntimeException("Failed to extract username", e);
        }
    }

    private Claims getClaims(String token) {
        logger.debug(">>> [JWT] Parsing token with secret key...");
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

