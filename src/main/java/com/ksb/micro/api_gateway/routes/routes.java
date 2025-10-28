package com.ksb.micro.api_gateway.routes;

import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

@Configuration
public class routes {

    @Bean
    public RouterFunction<ServerResponse> userProfileRoute(){
        return GatewayRouterFunctions.route("user-profile").route(RequestPredicates.path("/v1/banks/{bankId}/users"),
                HandlerFunctions.http("http://localhost:8081"))
                //chaining
                .route(RequestPredicates.path("/v1/banks/{bankId}/users/{userId}"),
                        HandlerFunctions.http("http://localhost:8081")).filter(CircuitBreakerFilterFunctions.circuitBreaker("userProfileCircuitBreaker", URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> profilePhotoRoute(){
        return GatewayRouterFunctions.route("profile_photo").route(RequestPredicates.path("/v1/banks/{bankId}/users/{userId}/photo"),
                HandlerFunctions.http("http://localhost:8082"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("profilePhotoCircuitBreaker", URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> fallbackRoute(){
        return GatewayRouterFunctions.route("fallbackRoute")
                .GET("/fallbackRoute", request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Service Unavailable")).build();
    }
}
