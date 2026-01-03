package com.teambind.springproject.circuitbreaker;

import com.teambind.springproject.exception.CustomException;
import com.teambind.springproject.exception.ErrorCode;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public final class FallbackHandler {

    private static final Logger log = LoggerFactory.getLogger(FallbackHandler.class);

    private FallbackHandler() {
    }

    /**
     * Circuit Breaker 에러 핸들링
     * - Circuit이 OPEN 상태: 서비스 일시 불가
     * - Timeout: 게이트웨이 타임아웃
     * - 기타: Bad Gateway
     */
    public static <T> Mono<T> handleError(String serviceName, Throwable throwable) {
        // Circuit Breaker가 OPEN 상태
        if (throwable instanceof CallNotPermittedException) {
            log.warn("[{}] Circuit breaker is OPEN. Service temporarily unavailable.", serviceName);
            return Mono.error(new CustomException(ErrorCode.SERVICE_UNAVAILABLE,
                    String.format("Service '%s' is temporarily unavailable. Please try again later.", serviceName)));
        }

        // 타임아웃
        if (throwable instanceof TimeoutException) {
            log.error("[{}] Request timeout: {}", serviceName, throwable.getMessage());
            return Mono.error(new CustomException(ErrorCode.GATEWAY_TIMEOUT,
                    String.format("Request to '%s' timed out.", serviceName)));
        }

        // 연결 실패
        if (throwable instanceof WebClientRequestException || throwable instanceof IOException) {
            log.error("[{}] Connection failed: {}", serviceName, throwable.getMessage());
            return Mono.error(new CustomException(ErrorCode.BAD_GATEWAY,
                    String.format("Failed to connect to '%s'.", serviceName)));
        }

        // 5xx 서버 에러
        if (throwable instanceof WebClientResponseException responseException) {
            if (responseException.getStatusCode().is5xxServerError()) {
                log.error("[{}] Server error {}: {}", serviceName,
                        responseException.getStatusCode(), responseException.getMessage());
                return Mono.error(new CustomException(ErrorCode.BAD_GATEWAY,
                        String.format("Service '%s' returned an error.", serviceName)));
            }
            // 4xx는 그대로 전파 (Circuit Breaker 트리거 안 함)
            return Mono.error(throwable);
        }

        // 기타 에러
        log.error("[{}] Unexpected error: {}", serviceName, throwable.getMessage(), throwable);
        return Mono.error(new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    /**
     * Circuit Breaker 상태 로깅
     */
    public static void logCircuitState(String serviceName, io.github.resilience4j.circuitbreaker.CircuitBreaker circuitBreaker) {
        var state = circuitBreaker.getState();
        var metrics = circuitBreaker.getMetrics();

        log.info("[{}] Circuit state: {}, Failure rate: {}%, Slow call rate: {}%",
                serviceName,
                state,
                metrics.getFailureRate(),
                metrics.getSlowCallRate());
    }
}
