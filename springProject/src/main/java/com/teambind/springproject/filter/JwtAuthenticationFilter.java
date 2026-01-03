package com.teambind.springproject.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teambind.springproject.auth.JwtTokenValidator;
import com.teambind.springproject.auth.Role;
import com.teambind.springproject.auth.TokenValidationResult;
import com.teambind.springproject.exception.ErrorCode;
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
import java.util.List;
import java.util.Map;

/**
 * 점주/관리자 전용 JWT 인증 필터
 * - PLACE_OWNER, ADMIN 역할만 접근 가능
 * - X-App-Type: PLACE_MANAGER 헤더 필수
 * - Rate Limit Filter 다음에 실행 (order: -100)
 */
@Component
public class JwtAuthenticationFilter implements WebFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final String X_APP_TYPE_HEADER = "X-App-Type";
    private static final String REQUIRED_APP_TYPE = "PLACE_MANAGER";

    // 인증이 필요없는 경로 (헬스체크, Swagger 등)
    private static final List<String> PUBLIC_PATHS = List.of(
            "/actuator",
            "/health",
            "/swagger-ui",
            "/v3/api-docs",
            "/webjars"
    );

    // 점주 인증 관련 경로 (로그인 등) - X-App-Type 검증은 하지만 JWT는 불필요
    private static final List<String> AUTH_PATHS = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/refresh"
    );

    private final JwtTokenValidator jwtTokenValidator;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtTokenValidator jwtTokenValidator, ObjectMapper objectMapper) {
        this.jwtTokenValidator = jwtTokenValidator;
        this.objectMapper = objectMapper;
    }

    @Override
    public int getOrder() {
        return -100; // Rate Limit Filter 다음에 실행
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().name();

        // OPTIONS 요청(CORS preflight)은 인증 없이 허용
        if ("OPTIONS".equals(method)) {
            log.debug("CORS preflight request: {} {}", method, path);
            return chain.filter(exchange);
        }

        // Public 경로 확인 (헬스체크, Swagger 등)
        if (isPublicPath(path)) {
            log.debug("Public path accessed: {}", path);
            return chain.filter(exchange);
        }

        // X-App-Type 헤더 검증 (Public 경로 제외한 모든 요청)
        String appType = request.getHeaders().getFirst(X_APP_TYPE_HEADER);
        if (!REQUIRED_APP_TYPE.equals(appType)) {
            log.warn("Invalid or missing X-App-Type header. Expected: {}, Got: {}, Path: {}",
                    REQUIRED_APP_TYPE, appType, path);
            return handleForbidden(exchange, "점주 앱에서만 접근할 수 있습니다 (X-App-Type: PLACE_MANAGER 필요)");
        }

        // 인증 경로는 X-App-Type만 검증하고 JWT는 불필요
        if (isAuthPath(path)) {
            log.debug("Auth path accessed with valid X-App-Type: {}", path);
            return chain.filter(exchange);
        }

        // Authorization 헤더에서 토큰 추출
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            return handleUnauthorized(exchange, ErrorCode.UNAUTHORIZED, "인증 토큰이 필요합니다");
        }

        String token = authHeader.substring(7); // "Bearer " 제거

        // 토큰 검증
        TokenValidationResult validationResult = jwtTokenValidator.validate(token);
        if (!validationResult.isValid()) {
            log.warn("Token validation failed for path: {}. Reason: {}", path, validationResult.getMessage());
            ErrorCode errorCode = mapToErrorCode(validationResult);
            return handleUnauthorized(exchange, errorCode, validationResult.getMessage());
        }

        // 토큰에서 사용자 정보 추출
        String userId = jwtTokenValidator.extractUserId(token);
        String roleString = jwtTokenValidator.extractRole(token);
        String deviceId = jwtTokenValidator.extractDeviceId(token);
        String placeId = jwtTokenValidator.extractPlaceId(token);

        // 사용자 정보 검증
        if (userId == null || userId.isEmpty()) {
            log.warn("Token validated but userId is missing for path: {}", path);
            return handleUnauthorized(exchange, ErrorCode.INVALID_TOKEN, "토큰에 사용자 정보가 없습니다");
        }

        // 역할 검증 - 점주/관리자만 허용
        Role role = Role.fromString(roleString);
        if (role == null || !role.canAccessPlaceOwnerGateway()) {
            log.warn("Access denied for user: {}, role: {} on path: {}", userId, roleString, path);
            return handleForbidden(exchange, "점주 또는 관리자만 접근할 수 있습니다");
        }

        log.debug("Authenticated PlaceOwner request - UserId: {}, Role: {}, PlaceId: {}, Path: {}",
                userId, role, placeId, path);

        // 요청 헤더에 사용자 정보 + 앱 타입 추가 (다운스트림 서비스로 전달)
        ServerHttpRequest mutatedRequest = request.mutate()
                .headers(headers -> {
                    // 기존 헤더 제거 (클라이언트 스푸핑 방지)
                    headers.remove("X-User-Id");
                    headers.remove("X-User-Role");
                    headers.remove("X-Device-Id");
                    headers.remove("X-Place-Id");
                    headers.remove("X-App-Type");
                })
                .header("X-User-Id", userId)
                .header("X-User-Role", role.name())
                .header("X-Device-Id", deviceId != null ? deviceId : "")
                .header("X-Place-Id", placeId != null ? placeId : "")
                .header("X-App-Type", REQUIRED_APP_TYPE)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        return chain.filter(mutatedExchange);
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private boolean isAuthPath(String path) {
        return AUTH_PATHS.stream().anyMatch(path::startsWith);
    }

    private ErrorCode mapToErrorCode(TokenValidationResult result) {
        return switch (result) {
            case EXPIRED -> ErrorCode.EXPIRED_TOKEN;
            case INVALID_SIGNATURE, MALFORMED, MISSING_CLAIMS, INVALID_FORMAT -> ErrorCode.INVALID_TOKEN;
            default -> ErrorCode.UNAUTHORIZED;
        };
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange, ErrorCode errorCode, String message) {
        return handleError(exchange, HttpStatus.UNAUTHORIZED, errorCode, message);
    }

    private Mono<Void> handleForbidden(ServerWebExchange exchange, String message) {
        return handleError(exchange, HttpStatus.FORBIDDEN, ErrorCode.UNAUTHORIZED, message);
    }

    private Mono<Void> handleError(
            ServerWebExchange exchange,
            HttpStatus status,
            ErrorCode errorCode,
            String message
    ) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> errorBody = Map.of(
                "success", false,
                "error", Map.of(
                        "code", errorCode.getCode(),
                        "message", message
                )
        );

        try {
            String json = objectMapper.writeValueAsString(errorBody);
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize error response", e);
            return response.setComplete();
        }
    }
}
