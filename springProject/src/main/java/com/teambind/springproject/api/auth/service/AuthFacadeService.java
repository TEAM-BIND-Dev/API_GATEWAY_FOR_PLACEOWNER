package com.teambind.springproject.api.auth.service;

import com.teambind.springproject.api.auth.client.AuthClient;
import com.teambind.springproject.api.auth.dto.request.LoginRequest;
import com.teambind.springproject.api.auth.dto.request.TokenRefreshRequest;
import com.teambind.springproject.api.auth.dto.response.LoginResponse;
import com.teambind.springproject.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthFacadeService {

    private final AuthClient authClient;

    public Mono<ApiResponse<LoginResponse>> login(LoginRequest request) {
        log.info("Login request - email: {}", request.getEmail());

        return authClient.login(request)
                .map(ApiResponse::ok)
                .doOnSuccess(r -> log.info("Login successful for email: {}", request.getEmail()))
                .doOnError(error -> log.error("Login failed for email: {}, error: {}", request.getEmail(), error.getMessage()));
    }

    public Mono<ApiResponse<LoginResponse>> refreshToken(TokenRefreshRequest request) {
        log.info("Token refresh request - deviceId: {}", request.getDeviceId());

        return authClient.refreshToken(request)
                .map(ApiResponse::ok)
                .doOnSuccess(r -> log.info("Token refresh successful for deviceId: {}", request.getDeviceId()))
                .doOnError(error -> log.error("Token refresh failed for deviceId: {}, error: {}", request.getDeviceId(), error.getMessage()));
    }
}
