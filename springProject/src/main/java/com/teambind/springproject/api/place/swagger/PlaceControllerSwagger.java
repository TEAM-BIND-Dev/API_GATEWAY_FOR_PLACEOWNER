package com.teambind.springproject.api.place.swagger;

import com.teambind.springproject.api.place.dto.request.PlaceRegisterRequest;
import com.teambind.springproject.api.place.dto.request.PlaceUpdateRequest;
import com.teambind.springproject.api.place.dto.response.PlaceResponse;
import com.teambind.springproject.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Tag(name = "Place", description = "공간 관리 API")
public interface PlaceControllerSwagger {

    @Operation(summary = "공간 등록", description = "새로운 공간을 등록합니다. 등록 성공 후 이미지 확정 처리가 수행됩니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "등록 성공",
                    content = @Content(schema = @Schema(implementation = PlaceResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 데이터 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    Mono<ApiResponse<PlaceResponse>> registerPlace(
            @Parameter(hidden = true) ServerHttpRequest request,
            PlaceRegisterRequest placeRegisterRequest
    );

    @Operation(summary = "내 공간 목록 조회", description = "로그인한 사용자가 등록한 공간 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    Mono<ApiResponse<List<PlaceResponse>>> getMyPlaces(
            @Parameter(hidden = true) ServerHttpRequest request
    );

    @Operation(summary = "공간 상세 조회", description = "공간 ID로 상세 정보를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PlaceResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "공간을 찾을 수 없음")
    })
    Mono<ApiResponse<PlaceResponse>> getPlace(
            @Parameter(description = "공간 ID", required = true) String placeId
    );

    @Operation(summary = "공간 정보 수정", description = "공간 정보를 수정합니다. 수정과 이미지 확정이 병렬로 처리됩니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = PlaceResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 데이터 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "공간을 찾을 수 없음")
    })
    Mono<ApiResponse<PlaceResponse>> updatePlace(
            @Parameter(description = "공간 ID", required = true) String placeId,
            @Parameter(hidden = true) ServerHttpRequest request,
            PlaceUpdateRequest updateRequest
    );

    @Operation(
            summary = "공간 위치 수정",
            description = """
                    공간의 위치 정보를 수정합니다.

                    **from (필수)**: 주소 데이터 출처
                    - KAKAO: 카카오 우편번호 서비스
                    - KAKAO_LOCAL: 카카오 로컬 REST API
                    - NAVER: 네이버맵
                    - MANUAL: 수동 입력

                    **addressData**: 외부 API 응답 원본 데이터 (JSON 객체)

                    **latitude (필수)**: 위도 (-90.0 ~ 90.0)

                    **longitude (필수)**: 경도 (-180.0 ~ 180.0)

                    **locationGuide**: 위치 안내 (최대 500자)
                    """
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    schema = @Schema(type = "object"),
                    examples = {
                            @ExampleObject(
                                    name = "카카오 주소 검색 예시",
                                    value = """
                                            {
                                              "from": "KAKAO",
                                              "addressData": {
                                                "address_name": "서울 강남구 테헤란로 123",
                                                "road_address": {
                                                  "address_name": "서울 강남구 테헤란로 123",
                                                  "building_name": "테헤란빌딩"
                                                }
                                              },
                                              "latitude": 37.5665,
                                              "longitude": 127.0092,
                                              "locationGuide": "지하철 2호선 강남역 3번 출구에서 도보 5분"
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "수동 입력 예시",
                                    value = """
                                            {
                                              "from": "MANUAL",
                                              "latitude": 37.5665,
                                              "longitude": 127.0092,
                                              "locationGuide": "건물 뒤편 주차장 이용"
                                            }
                                            """
                            )
                    }
            )
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "위치 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 데이터 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "공간을 찾을 수 없음")
    })
    Mono<ApiResponse<Map<String, String>>> updateLocation(
            @Parameter(description = "공간 ID", required = true) String placeId,
            @Parameter(hidden = true) ServerHttpRequest request,
            Map<String, Object> requestBody
    );
}
