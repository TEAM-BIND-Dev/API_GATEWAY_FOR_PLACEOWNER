package com.teambind.springproject.api.timeslot.swagger;

import com.teambind.springproject.api.timeslot.dto.request.ClosedDateSetupRequest;
import com.teambind.springproject.api.timeslot.dto.request.MultiSlotReservationRequest;
import com.teambind.springproject.api.timeslot.dto.request.OperatingHoursUpdateRequest;
import com.teambind.springproject.api.timeslot.dto.request.RoomOperatingPolicySetupRequest;
import com.teambind.springproject.api.timeslot.dto.request.SlotReservationRequest;
import com.teambind.springproject.api.timeslot.dto.response.AvailableSlotResponse;
import com.teambind.springproject.api.timeslot.dto.response.ClosedDateSetupResponse;
import com.teambind.springproject.api.timeslot.dto.response.EnsureSlotsResponse;
import com.teambind.springproject.api.timeslot.dto.response.MultiSlotReservationResponse;
import com.teambind.springproject.api.timeslot.dto.response.OperatingHoursUpdateResponse;
import com.teambind.springproject.api.timeslot.dto.response.RoomSetupResponse;
import com.teambind.springproject.api.timeslot.dto.response.SlotGenerationStatusResponse;
import com.teambind.springproject.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "TimeSlot", description = "시간 슬롯 관리 API")
public interface TimeSlotControllerSwagger {

    @Operation(
            summary = "예약 가능 슬롯 조회",
            description = "특정 룸의 특정 날짜에 예약 가능한 슬롯 목록을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    Mono<ApiResponse<List<AvailableSlotResponse>>> getAvailableSlots(
            @Parameter(description = "Room ID", required = true) Long roomId,
            @Parameter(description = "조회 날짜 (yyyy-MM-dd)", required = true) LocalDate date
    );

    @Operation(
            summary = "단일 슬롯 예약",
            description = "특정 시간 슬롯을 예약 대기 상태로 변경합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "예약 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "슬롯 이미 예약됨")
    })
    Mono<ApiResponse<Void>> createReservation(SlotReservationRequest request);

    @Operation(
            summary = "다중 슬롯 예약",
            description = """
                    특정 날짜의 여러 시간 슬롯을 한 번에 예약 대기 상태로 변경합니다.
                    예약 ID는 자동으로 생성됩니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "예약 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "일부 슬롯 이미 예약됨")
    })
    Mono<ApiResponse<MultiSlotReservationResponse>> createMultiSlotReservation(MultiSlotReservationRequest request);

    @Operation(
            summary = "룸 운영정책 설정",
            description = """
                    룸의 운영 정책을 설정하고 슬롯 생성을 요청합니다.
                    슬롯 생성은 비동기로 처리되며, 202 Accepted 응답과 함께 requestId가 반환됩니다.

                    **slotUnit**:
                    - HOUR: 1시간 단위
                    - HALF_HOUR: 30분 단위

                    **recurrencePattern**:
                    - EVERY_WEEK: 매주
                    - ODD_WEEK: 홀수 주
                    - EVEN_WEEK: 짝수 주
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "설정 요청 수락"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    Mono<ApiResponse<RoomSetupResponse>> setupRoom(
            @Parameter(hidden = true) ServerHttpRequest request,
            RoomOperatingPolicySetupRequest setupRequest
    );

    @Operation(
            summary = "슬롯 생성 상태 조회",
            description = "비동기 슬롯 생성 작업의 상태를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상태 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "요청 ID를 찾을 수 없음")
    })
    Mono<ApiResponse<SlotGenerationStatusResponse>> getSetupStatus(
            @Parameter(description = "요청 ID", required = true) String requestId
    );

    @Operation(
            summary = "휴무일 설정",
            description = """
                    룸의 휴무일을 설정합니다.
                    기존 슬롯의 상태가 비동기로 CLOSED로 변경됩니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "설정 요청 수락"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    Mono<ApiResponse<ClosedDateSetupResponse>> setupClosedDates(
            @Parameter(hidden = true) ServerHttpRequest request,
            ClosedDateSetupRequest setupRequest
    );

    @Operation(
            summary = "슬롯 보완 생성",
            description = """
                    오늘부터 30일 이후까지 슬롯을 확인하고, 누락된 날짜에만 새로 생성합니다.
                    Room이 새로 등록되거나 슬롯이 누락된 경우 보완하는 용도로 사용됩니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "보완 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Room을 찾을 수 없음")
    })
    Mono<ApiResponse<EnsureSlotsResponse>> ensureSlots(
            @Parameter(description = "Room ID", required = true) Long roomId,
            @Parameter(hidden = true) ServerHttpRequest request
    );

    @Operation(
            summary = "운영시간 업데이트",
            description = """
                    룸의 운영 시간을 업데이트하고 슬롯을 재생성합니다.
                    기존 AVAILABLE 슬롯만 삭제하고 새 운영 시간 기준으로 재생성합니다.
                    CLOSED, RESERVED, PENDING 슬롯은 유지됩니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "업데이트 요청 수락"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    Mono<ApiResponse<OperatingHoursUpdateResponse>> updateOperatingHours(
            @Parameter(hidden = true) ServerHttpRequest request,
            OperatingHoursUpdateRequest updateRequest
    );
}
