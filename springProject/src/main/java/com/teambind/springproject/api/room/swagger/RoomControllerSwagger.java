package com.teambind.springproject.api.room.swagger;

import com.teambind.springproject.api.room.dto.Status;
import com.teambind.springproject.api.room.dto.request.RoomCreateRequest;
import com.teambind.springproject.api.room.dto.request.RoomUpdateRequest;
import com.teambind.springproject.api.room.dto.response.ReservationFieldResponse;
import com.teambind.springproject.api.room.dto.response.RoomDetailResponse;
import com.teambind.springproject.api.room.dto.response.RoomSimpleResponse;
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

@Tag(name = "Room", description = "Room 관리 API")
public interface RoomControllerSwagger {

    @Operation(
            summary = "Room 등록",
            description = """
                    새로운 Room을 등록합니다. 등록 성공 후 이미지 확정이 순차적으로 처리됩니다.

                    **timeSlot**: 시간 단위
                    - HOUR: 1시간
                    - HALFHOUR: 30분

                    **imageIds**: 이미지 서버에 업로드된 이미지 ID 목록 (최대 10개)
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "등록 성공",
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 데이터 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    Mono<ApiResponse<Long>> createRoom(
            @Parameter(hidden = true) ServerHttpRequest request,
            RoomCreateRequest createRequest
    );

    @Operation(
            summary = "Room 수정",
            description = """
                    Room 정보를 수정합니다. 수정과 이미지 확정이 병렬로 처리됩니다.

                    **수정 가능 필드**: roomName, timeSlot, maxOccupancy, furtherDetails, cautionDetails, keywordIds, imageIds
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 데이터 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Room을 찾을 수 없음")
    })
    Mono<ApiResponse<Long>> updateRoom(
            @Parameter(description = "Room ID", required = true) Long roomId,
            @Parameter(hidden = true) ServerHttpRequest request,
            RoomUpdateRequest updateRequest
    );

    @Operation(
            summary = "Room 상세 조회",
            description = """
                    Room 상세 정보를 조회합니다.

                    **응답 필드**:
                    - status: OPEN / CLOSE / PENDING
                    - images: 이미지 정보 목록 (imageId, imageUrl, sequence)
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = RoomDetailResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Room을 찾을 수 없음")
    })
    Mono<ApiResponse<RoomDetailResponse>> getRoom(
            @Parameter(description = "Room ID", required = true) Long roomId,
            @Parameter(hidden = true) ServerHttpRequest request
    );

    @Operation(
            summary = "Place별 Room 목록 조회",
            description = "특정 Place에 속한 Room 목록을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    Mono<ApiResponse<List<RoomSimpleResponse>>> getRoomsByPlace(
            @Parameter(description = "Place ID", required = true) Long placeId,
            @Parameter(hidden = true) ServerHttpRequest request
    );

    @Operation(
            summary = "Room 상태 변경",
            description = """
                    Room 상태를 변경합니다.

                    **status**:
                    - OPEN: 열림 (일반 사용자 조회 가능)
                    - CLOSE: 닫힘
                    - PENDING: 대기 중
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상태 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Room을 찾을 수 없음")
    })
    Mono<ApiResponse<Long>> updateRoomStatus(
            @Parameter(description = "Room ID", required = true) Long roomId,
            @Parameter(description = "변경할 상태", required = true) Status status,
            @Parameter(hidden = true) ServerHttpRequest request
    );

    @Operation(summary = "Room 삭제", description = "Room을 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Room을 찾을 수 없음")
    })
    Mono<ApiResponse<Void>> deleteRoom(
            @Parameter(description = "Room ID", required = true) Long roomId,
            @Parameter(hidden = true) ServerHttpRequest request
    );

    @Operation(summary = "예약 필드 조회", description = "Room의 예약 필드 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Room을 찾을 수 없음")
    })
    Mono<ApiResponse<List<ReservationFieldResponse>>> getReservationFields(
            @Parameter(description = "Room ID", required = true) Long roomId
    );

    @Operation(
            summary = "예약 필드 추가",
            description = """
                    Room에 예약 필드를 추가합니다. 최대 10개까지 추가 가능합니다.

                    **inputType**:
                    - TEXT: 텍스트 입력
                    - NUMBER: 숫자 입력
                    - SELECT: 선택
                    """
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    schema = @Schema(type = "object"),
                    examples = @ExampleObject(
                            name = "예약 필드 추가 예시",
                            value = """
                                    {
                                      "title": "예약자 이름",
                                      "inputType": "TEXT",
                                      "required": true,
                                      "maxLength": 50,
                                      "sequence": 1
                                    }
                                    """
                    )
            )
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "추가 성공",
                    content = @Content(schema = @Schema(implementation = ReservationFieldResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 데이터 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    Mono<ApiResponse<ReservationFieldResponse>> addReservationField(
            @Parameter(description = "Room ID", required = true) Long roomId,
            @Parameter(hidden = true) ServerHttpRequest request,
            Map<String, Object> requestBody
    );

    @Operation(summary = "예약 필드 삭제", description = "Room의 예약 필드를 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "필드를 찾을 수 없음")
    })
    Mono<ApiResponse<Void>> deleteReservationField(
            @Parameter(description = "Room ID", required = true) Long roomId,
            @Parameter(description = "Field ID", required = true) Long fieldId,
            @Parameter(hidden = true) ServerHttpRequest request
    );
}
