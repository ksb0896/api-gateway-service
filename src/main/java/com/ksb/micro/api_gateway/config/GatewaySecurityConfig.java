package com.ksb.micro.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/auth/**").permitAll()
                        .pathMatchers("/v3/api-docs/**").permitAll()
                        .pathMatchers("/swagger-ui.html").permitAll()
                        .pathMatchers("/swagger-ui/**").permitAll()
                        .pathMatchers("/webjars/**").permitAll()
                        .pathMatchers("/error").permitAll()
                        .anyExchange().permitAll()  // Allow all - let gateway filter handle auth
                );

        return http.build();
    }
}
