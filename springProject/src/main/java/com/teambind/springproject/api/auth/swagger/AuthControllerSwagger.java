package com.teambind.springproject.api.auth.swagger;

import com.teambind.springproject.api.auth.dto.request.LoginRequest;
import com.teambind.springproject.api.auth.dto.request.TokenRefreshRequest;
import com.teambind.springproject.api.auth.dto.response.LoginResponse;
import com.teambind.springproject.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@Tag(name = "Auth", description = "인증 API (로그인, 토큰 갱신)")
public interface AuthControllerSwagger {

    @Operation(summary = "로그인", description = "이메일/비밀번호로 로그인합니다. PLACE_OWNER 역할만 로그인 가능합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "PLACE_OWNER 권한 없음")
    })
    Mono<ApiResponse<LoginResponse>> login(LoginRequest request);

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰으로 새로운 액세스/리프레시 토큰을 발급받습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "토큰 갱신 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "PLACE_OWNER 권한 없음")
    })
    Mono<ApiResponse<LoginResponse>> refreshToken(TokenRefreshRequest request);
}
