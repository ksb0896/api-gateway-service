package com.ksb.micro.api_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    @RequestMapping(value = "/fallback/user-profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> userProfileFallback(){
        return Mono.just("{\"status\":\"SERVICE_UNAVAILABLE\",\"message\":\"User Profile Service is currently unavailable. Try after some time.\",\"httpStatus\":" + HttpStatus.SERVICE_UNAVAILABLE.value() + "}")
                .doOnNext(s -> System.err.println("Circuit Breaker: User profile fallback is executed at gateway"));
    }

    @RequestMapping(value = "/fallback/profile-photo", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> profilePhotoFallback(){
        return Mono.just("{\"status\":\"SERVICE_UNAVAILABLE\",\"message\":\"Profile Photo Service is currently unavailable. Try after some time.\",\"httpStatus\":" + HttpStatus.SERVICE_UNAVAILABLE.value() + "}")
                .doOnNext(s -> System.err.println("Circuit Breaker: Profile photo fallback is executed at gateway"));
    }
}
