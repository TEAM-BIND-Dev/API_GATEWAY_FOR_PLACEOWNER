package com.teambind.springproject.controller;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public HealthController(
            ReactiveStringRedisTemplate redisTemplate,
            CircuitBreakerRegistry circuitBreakerRegistry
    ) {
        this.redisTemplate = redisTemplate;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @GetMapping("/health")
    public Mono<ResponseEntity<Map<String, Object>>> health() {
        return checkRedis()
                .map(redisStatus -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "UP");
                    response.put("timestamp", LocalDateTime.now().toString());
                    response.put("service", "placeowner-gateway");

                    Map<String, Object> components = new HashMap<>();
                    components.put("redis", redisStatus);
                    components.put("circuitBreakers", getCircuitBreakerStatus());

                    response.put("components", components);

                    return ResponseEntity.ok(response);
                })
                .onErrorResume(e -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "DOWN");
                    response.put("timestamp", LocalDateTime.now().toString());
                    response.put("error", e.getMessage());

                    return Mono.just(ResponseEntity.status(503).body(response));
                });
    }

    private Mono<Map<String, Object>> checkRedis() {
        return redisTemplate.getConnectionFactory()
                .getReactiveConnection()
                .ping()
                .map(pong -> {
                    Map<String, Object> status = new HashMap<>();
                    status.put("status", "UP");
                    status.put("ping", pong);
                    return status;
                })
                .onErrorResume(e -> {
                    Map<String, Object> status = new HashMap<>();
                    status.put("status", "DOWN");
                    status.put("error", e.getMessage());
                    return Mono.just(status);
                });
    }

    private Map<String, Object> getCircuitBreakerStatus() {
        Map<String, Object> cbStatus = new HashMap<>();

        circuitBreakerRegistry.getAllCircuitBreakers().forEach(cb -> {
            Map<String, Object> info = new HashMap<>();
            info.put("state", cb.getState().name());
            info.put("failureRate", cb.getMetrics().getFailureRate());
            info.put("slowCallRate", cb.getMetrics().getSlowCallRate());
            info.put("bufferedCalls", cb.getMetrics().getNumberOfBufferedCalls());
            cbStatus.put(cb.getName(), info);
        });

        return cbStatus;
    }
}
