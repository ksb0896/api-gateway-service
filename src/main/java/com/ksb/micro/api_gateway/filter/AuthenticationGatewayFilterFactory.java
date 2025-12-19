/*
This file will intercept all requests that are not /login and check for a valid JWT
 */
package com.ksb.micro.api_gateway.filter;

import com.ksb.micro.api_gateway.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Component
public class AuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthenticationGatewayFilterFactory.Config> {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationGatewayFilterFactory.class);

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationGatewayFilterFactory() {
        super(Config.class);
        logger.info("=== AuthenticationGatewayFilterFactory initialized ===");
    }

    public static class Config {
        // Put the configuration properties for your filter here
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().value();
            logger.info(">>> [GATEWAY FILTER] Processing request to: {}", path);

            // Skip authentication for Swagger/OpenAPI documentation paths
            if (path.contains("/v3/api-docs") || path.contains("/swagger-ui") || path.contains("/webjars")) {
                logger.debug(">>> [GATEWAY FILTER] Swagger/OpenAPI path detected, skipping authentication: {}", path);
                return chain.filter(exchange);
            }

            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                logger.warn(">>> [GATEWAY FILTER] Missing Authorization header for path: {}", path);
                return this.onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
            }

            List<String> authHeaders = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
            if (authHeaders == null || authHeaders.isEmpty()) {
                logger.warn(">>> [GATEWAY FILTER] Empty Authorization header for path: {}", path);
                return this.onError(exchange, "Missing Authorization header value", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = authHeaders.get(0);
            logger.info(">>> [GATEWAY FILTER] Authorization header present, length: {}", authHeader.length());

            if (!authHeader.startsWith("Bearer ")) {
                logger.warn(">>> [GATEWAY FILTER] Invalid Authorization scheme (doesn't start with 'Bearer ') for path: {}", path);
                return this.onError(exchange, "Invalid Authorization scheme", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);
            logger.info(">>> [GATEWAY FILTER] Token extracted, length: {}, first 50 chars: {}", token.length(), token.substring(0, Math.min(50, token.length())));

            try {
                logger.info(">>> [GATEWAY FILTER] Starting token validation...");
                boolean isValid = jwtUtil.validateToken(token);

                if (isValid) {
                    String username = jwtUtil.getUsernameFromToken(token);
                    logger.info(">>> [GATEWAY FILTER] ✓ TOKEN VALID - Username: {} on path: {}", username, path);

                    // 1. Authentication object
                    List<GrantedAuthority> authorities = Collections.emptyList();
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            username, null, authorities
                    );

                    // 2. Mutate the request: Remove the Authorization header and add custom identity header
                    ServerWebExchange modifiedExchange = exchange.mutate()
                            .request(exchange.getRequest().mutate()
                                    .header("X-Authenticated-User", username)
                                    .headers(headers -> headers.remove(HttpHeaders.AUTHORIZATION))
                                    .build())
                            .build();

                    // 3. Set the Authentication object in the Reactive Security Context
                    logger.debug(">>> [GATEWAY FILTER] Passing request to next filter with authenticated user: {}", username);
                    return chain.filter(modifiedExchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));

                } else {
                    logger.warn(">>> [GATEWAY FILTER] ✗ TOKEN INVALID or EXPIRED for path: {}", path);
                    return this.onError(exchange, "Invalid or expired JWT token", HttpStatus.UNAUTHORIZED);
                }
            } catch (Exception e) {
                logger.error(">>> [GATEWAY FILTER] ✗ JWT validation EXCEPTION for path: {} - Error: {} - Cause: {}",
                    path, e.getMessage(), e.getCause(), e);
                return this.onError(exchange, "JWT validation failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        logger.error(">>> [GATEWAY FILTER] ERROR RESPONSE: Status={}, Message={}", httpStatus, err);
        return exchange.getResponse().setComplete();
    }
}