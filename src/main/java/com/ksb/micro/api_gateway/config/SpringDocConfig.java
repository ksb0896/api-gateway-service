package com.ksb.micro.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

import java.util.Arrays;

/**
 * SpringDoc OpenAPI Configuration for API Gateway.
 * Configures OpenAPI documentation aggregation from multiple microservices.
 */
@Configuration
public class SpringDocConfig {

    /**
     * Configures the OpenAPI bean for the API Gateway.
     * Sets up basic information and servers.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI();

        // Configure basic API information
        openAPI.info(new Info()
                .title("API Gateway - Microservices Documentation")
                .version("1.0.0")
                .description("Aggregated API documentation for all microservices")
        );

        // Add server information
        openAPI.servers(Arrays.asList(
                new Server()
                        .url("http://localhost:8081")
                        .description("User Profile Service"),
                new Server()
                        .url("http://localhost:8082")
                        .description("Profile Photo Service"),
                new Server()
                        .url("http://localhost:8083")
                        .description("Auth Service")
        ));

        return openAPI;
    }
}

