package com.teambind.springproject.api.auth.controller;

import com.teambind.springproject.api.auth.dto.request.LoginRequest;
import com.teambind.springproject.api.auth.dto.request.TokenRefreshRequest;
import com.teambind.springproject.api.auth.dto.response.LoginResponse;
import com.teambind.springproject.api.auth.service.AuthFacadeService;
import com.teambind.springproject.api.auth.swagger.AuthControllerSwagger;
import com.teambind.springproject.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerSwagger {

    private final AuthFacadeService authFacadeService;

    @Override
    @PostMapping("/login")
    public Mono<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return authFacadeService.login(request);
    }

    @Override
    @PostMapping("/login/refreshToken")
    public Mono<ApiResponse<LoginResponse>> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return authFacadeService.refreshToken(request);
    }
}
