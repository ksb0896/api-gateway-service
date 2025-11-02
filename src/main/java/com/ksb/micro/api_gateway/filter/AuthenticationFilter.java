/*
This file will intercept all requests that are not /login and check for a valid JWT
 */
package com.ksb.micro.api_gateway.filter;

import com.ksb.micro.api_gateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            System.out.println("Incoming request: " + path);

            //Allow public routes
            if (path.startsWith("/auth")) {
                System.out.println("Skipping auth for public route");
                return chain.filter(exchange);
            }

            //Check Authorization header
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, HttpStatus.UNAUTHORIZED, "Missing Authorization header");
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, HttpStatus.UNAUTHORIZED, "Invalid Authorization header");
            }

            String token = authHeader.substring(7);

            try {
                if (!jwtUtil.validateToken(token)) {
                    return onError(exchange, HttpStatus.UNAUTHORIZED, "Invalid token");
                }

                //Add username to downstream headers
                String username = jwtUtil.getUsernameFromToken(token);

                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-Authenticated-User", username)
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                return onError(exchange, HttpStatus.UNAUTHORIZED, "Token validation failed");
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status, String message) {
        System.out.println("AUTH ERROR: " + message);
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }

    public static class Config {}
}