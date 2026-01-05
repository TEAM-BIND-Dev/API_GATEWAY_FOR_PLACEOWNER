package com.teambind.springproject.api.auth.client;

import com.teambind.springproject.api.auth.dto.request.LoginRequest;
import com.teambind.springproject.api.auth.dto.request.TokenRefreshRequest;
import com.teambind.springproject.api.auth.dto.response.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthClient {

    private static final String APP_TYPE_HEADER = "X-App-Type";
    private static final String APP_TYPE_PLACE_MANAGER = "PLACE_MANAGER";

    private final WebClient authWebClient;

    public AuthClient(@Qualifier("authWebClient") WebClient authWebClient) {
        this.authWebClient = authWebClient;
    }

    public Mono<LoginResponse> login(LoginRequest request) {
        log.debug("Login request for email: {}", request.getEmail());

        return authWebClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .header(APP_TYPE_HEADER, APP_TYPE_PLACE_MANAGER)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(LoginResponse.class)
                .doOnSuccess(response -> log.debug("Login successful"))
                .doOnError(error -> log.error("Login failed: {}", error.getMessage()));
    }

    public Mono<LoginResponse> refreshToken(TokenRefreshRequest request) {
        log.debug("Token refresh request for deviceId: {}", request.getDeviceId());

        return authWebClient.post()
                .uri("/api/v1/auth/login/refreshToken")
                .contentType(MediaType.APPLICATION_JSON)
                .header(APP_TYPE_HEADER, APP_TYPE_PLACE_MANAGER)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(LoginResponse.class)
                .doOnSuccess(response -> log.debug("Token refresh successful"))
                .doOnError(error -> log.error("Token refresh failed: {}", error.getMessage()));
    }
}
