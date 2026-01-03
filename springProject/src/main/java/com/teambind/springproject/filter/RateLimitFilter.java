package com.teambind.springproject.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teambind.springproject.config.properties.RateLimitProperties;
import com.teambind.springproject.exception.ErrorCode;
import com.teambind.springproject.ratelimit.RateLimitResult;
import com.teambind.springproject.ratelimit.RateLimiterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class RateLimitFilter implements WebFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);

    private static final String X_USER_ID = "X-User-Id";
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String X_REAL_IP = "X-Real-IP";
    private static final String X_RATELIMIT_LIMIT = "X-RateLimit-Limit";
    private static final String X_RATELIMIT_REMAINING = "X-RateLimit-Remaining";
    private static final String RETRY_AFTER = "Retry-After";

    private final RateLimiterService rateLimiterService;
    private final RateLimitProperties rateLimitProperties;
    private final ObjectMapper objectMapper;

    public RateLimitFilter(
            RateLimiterService rateLimiterService,
            RateLimitProperties rateLimitProperties,
            ObjectMapper objectMapper
    ) {
        this.rateLimiterService = rateLimiterService;
        this.rateLimitProperties = rateLimitProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 200;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (!rateLimitProperties.isEnabled()) {
            return chain.filter(exchange);
        }

        String path = exchange.getRequest().getPath().value();

        // Skip rate limiting for health check and actuator
        if (path.startsWith("/actuator") || path.equals("/health")) {
            return chain.filter(exchange);
        }

        String key = resolveKey(exchange);
        RateLimitProperties.Policy policy = rateLimitProperties.getPolicyForPath(path);

        return rateLimiterService.isAllowed(key, policy)
                .flatMap(result -> {
                    addRateLimitHeaders(exchange, result, policy);

                    if (result.allowed()) {
                        return chain.filter(exchange);
                    } else {
                        return handleRateLimitExceeded(exchange, result);
                    }
                });
    }

    private String resolveKey(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();

        // 1. 인증된 사용자는 userId 기반
        String userId = request.getHeaders().getFirst(X_USER_ID);
        if (userId != null && !userId.isBlank()) {
            return "user:" + userId;
        }

        // 2. 미인증 사용자는 IP 기반
        return "ip:" + getClientIp(request);
    }

    private String getClientIp(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();

        // X-Forwarded-For (프록시/로드밸런서 뒤)
        String xff = headers.getFirst(X_FORWARDED_FOR);
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }

        // X-Real-IP (nginx)
        String realIp = headers.getFirst(X_REAL_IP);
        if (realIp != null && !realIp.isBlank()) {
            return realIp;
        }

        // 직접 연결
        if (request.getRemoteAddress() != null) {
            return request.getRemoteAddress().getAddress().getHostAddress();
        }

        return "unknown";
    }

    private void addRateLimitHeaders(
            ServerWebExchange exchange,
            RateLimitResult result,
            RateLimitProperties.Policy policy
    ) {
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = response.getHeaders();

        headers.add(X_RATELIMIT_LIMIT, String.valueOf(policy.getLimit()));

        if (result.remainingTokens() >= 0) {
            headers.add(X_RATELIMIT_REMAINING, String.valueOf(result.remainingTokens()));
        }

        if (!result.allowed() && result.resetAfterSeconds() > 0) {
            headers.add(RETRY_AFTER, String.valueOf(result.resetAfterSeconds()));
        }
    }

    private Mono<Void> handleRateLimitExceeded(ServerWebExchange exchange, RateLimitResult result) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorCode errorCode = ErrorCode.RATE_LIMIT_EXCEEDED;
        Map<String, Object> errorBody = Map.of(
                "success", false,
                "error", Map.of(
                        "code", errorCode.getCode(),
                        "message", errorCode.getMessage(),
                        "retryAfter", result.resetAfterSeconds()
                )
        );

        try {
            String json = objectMapper.writeValueAsString(errorBody);
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize rate limit error response", e);
            return response.setComplete();
        }
    }
}
