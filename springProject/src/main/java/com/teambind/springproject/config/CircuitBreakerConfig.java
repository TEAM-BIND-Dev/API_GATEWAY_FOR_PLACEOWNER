package com.teambind.springproject.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CircuitBreakerConfig {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public CircuitBreakerConfig(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @Bean
    public CircuitBreaker placeServiceCircuitBreaker() {
        return circuitBreakerRegistry.circuitBreaker("place-service");
    }

    @Bean
    public CircuitBreaker roomServiceCircuitBreaker() {
        return circuitBreakerRegistry.circuitBreaker("room-service");
    }

    @Bean
    public CircuitBreaker reservationServiceCircuitBreaker() {
        return circuitBreakerRegistry.circuitBreaker("reservation-service");
    }

    @Bean
    public CircuitBreaker userServiceCircuitBreaker() {
        return circuitBreakerRegistry.circuitBreaker("user-service");
    }

    @Bean
    public CircuitBreaker paymentServiceCircuitBreaker() {
        return circuitBreakerRegistry.circuitBreaker("payment-service");
    }

    @Bean
    public CircuitBreaker notificationServiceCircuitBreaker() {
        return circuitBreakerRegistry.circuitBreaker("notification-service");
    }
}
