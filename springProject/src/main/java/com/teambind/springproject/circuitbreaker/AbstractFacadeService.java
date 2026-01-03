package com.teambind.springproject.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * 백엔드 서비스 호출을 위한 추상 Facade 클래스
 * - Circuit Breaker 자동 적용
 * - 타임아웃 처리
 * - 에러 핸들링
 */
public abstract class AbstractFacadeService {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final WebClient webClient;
    protected final CircuitBreaker circuitBreaker;
    protected final String serviceName;
    protected final Duration timeout;

    protected AbstractFacadeService(
            WebClient webClient,
            CircuitBreaker circuitBreaker,
            String serviceName,
            Duration timeout
    ) {
        this.webClient = webClient;
        this.circuitBreaker = circuitBreaker;
        this.serviceName = serviceName;
        this.timeout = timeout;
    }

    protected AbstractFacadeService(
            WebClient webClient,
            CircuitBreaker circuitBreaker,
            String serviceName
    ) {
        this(webClient, circuitBreaker, serviceName, Duration.ofSeconds(30));
    }

    /**
     * Circuit Breaker가 적용된 Mono 반환
     */
    protected <T> Mono<T> withCircuitBreaker(Mono<T> mono) {
        return mono
                .timeout(timeout)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .doOnSubscribe(s -> log.debug("[{}] Request started", serviceName))
                .doOnSuccess(r -> log.debug("[{}] Request succeeded", serviceName))
                .doOnError(e -> log.warn("[{}] Request failed: {}", serviceName, e.getMessage()))
                .onErrorResume(e -> FallbackHandler.handleError(serviceName, e));
    }

    /**
     * Circuit Breaker가 적용된 Flux 반환
     */
    protected <T> Flux<T> withCircuitBreaker(Flux<T> flux) {
        return flux
                .timeout(timeout)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .doOnSubscribe(s -> log.debug("[{}] Stream request started", serviceName))
                .doOnComplete(() -> log.debug("[{}] Stream request completed", serviceName))
                .doOnError(e -> log.warn("[{}] Stream request failed: {}", serviceName, e.getMessage()))
                .onErrorResume(e -> FallbackHandler.<T>handleError(serviceName, e).flux());
    }

    /**
     * Circuit Breaker 상태 조회
     */
    public CircuitBreaker.State getCircuitState() {
        return circuitBreaker.getState();
    }

    /**
     * Circuit Breaker 메트릭 로깅
     */
    public void logMetrics() {
        FallbackHandler.logCircuitState(serviceName, circuitBreaker);
    }

    /**
     * 서비스명 반환
     */
    public String getServiceName() {
        return serviceName;
    }
}
