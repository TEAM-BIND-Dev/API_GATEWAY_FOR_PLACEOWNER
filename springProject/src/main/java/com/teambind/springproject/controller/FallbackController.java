package com.teambind.springproject.controller;

import com.teambind.springproject.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Circuit Breaker Fallback 처리 컨트롤러
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public Mono<ResponseEntity<Map<String, Object>>> authFallback() {
        return createFallbackResponse("auth-service", ErrorCode.SERVICE_UNAVAILABLE);
    }

    @GetMapping("/image")
    public Mono<ResponseEntity<Map<String, Object>>> imageFallback() {
        return createFallbackResponse("image-service", ErrorCode.SERVICE_UNAVAILABLE);
    }

    @GetMapping("/place")
    public Mono<ResponseEntity<Map<String, Object>>> placeFallback() {
        return createFallbackResponse("place-service", ErrorCode.SERVICE_UNAVAILABLE);
    }

    @GetMapping("/room")
    public Mono<ResponseEntity<Map<String, Object>>> roomFallback() {
        return createFallbackResponse("room-service", ErrorCode.SERVICE_UNAVAILABLE);
    }

    @GetMapping("/reservation")
    public Mono<ResponseEntity<Map<String, Object>>> reservationFallback() {
        return createFallbackResponse("reservation-service", ErrorCode.SERVICE_UNAVAILABLE);
    }

    @GetMapping("/coupon")
    public Mono<ResponseEntity<Map<String, Object>>> couponFallback() {
        return createFallbackResponse("coupon-service", ErrorCode.SERVICE_UNAVAILABLE);
    }

    @GetMapping("/chat")
    public Mono<ResponseEntity<Map<String, Object>>> chatFallback() {
        return createFallbackResponse("chat-service", ErrorCode.SERVICE_UNAVAILABLE);
    }

    @GetMapping("/notification")
    public Mono<ResponseEntity<Map<String, Object>>> notificationFallback() {
        return createFallbackResponse("notification-service", ErrorCode.SERVICE_UNAVAILABLE);
    }

    private Mono<ResponseEntity<Map<String, Object>>> createFallbackResponse(
            String serviceName,
            ErrorCode errorCode
    ) {
        Map<String, Object> body = Map.of(
                "success", false,
                "error", Map.of(
                        "code", errorCode.getCode(),
                        "message", String.format("'%s' 서비스가 일시적으로 이용 불가합니다. 잠시 후 다시 시도해주세요.", serviceName),
                        "service", serviceName
                )
        );

        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(body));
    }
}
