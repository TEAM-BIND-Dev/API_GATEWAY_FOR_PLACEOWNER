package com.teambind.springproject.api.place.swagger;

import com.teambind.springproject.api.place.dto.request.PlaceRegisterRequest;
import com.teambind.springproject.api.place.dto.response.PlaceResponse;
import com.teambind.springproject.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

@Tag(name = "Place", description = "공간 관리 API")
public interface PlaceControllerSwagger {

    @Operation(
            summary = "공간 등록",
            description = "새로운 공간을 등록합니다. 점주(PLACE_OWNER) 또는 관리자(ADMIN)만 등록 가능합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "등록 성공",
                    content = @Content(schema = @Schema(implementation = PlaceResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청 데이터 검증 실패"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음"
            )
    })
    Mono<ApiResponse<PlaceResponse>> registerPlace(
            @Parameter(hidden = true) ServerHttpRequest request,
            PlaceRegisterRequest placeRegisterRequest
    );
}
