package com.teambind.springproject.api.timeslot.service;

import com.teambind.springproject.api.timeslot.client.TimeSlotClient;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeSlotFacadeService {

    private final TimeSlotClient timeSlotClient;

    /**
     * 예약 가능 슬롯 조회.
     */
    public Mono<ApiResponse<List<AvailableSlotResponse>>> getAvailableSlots(Long roomId, LocalDate date) {
        log.info("Get available slots - roomId: {}, date: {}", roomId, date);

        return timeSlotClient.getAvailableSlots(roomId, date)
                .map(ApiResponse::ok)
                .doOnSuccess(r -> log.info("Available slots retrieved for roomId: {}", roomId))
                .doOnError(error -> log.error("Failed to get available slots: {}", error.getMessage()));
    }

    /**
     * 단일 슬롯 예약 생성.
     */
    public Mono<ApiResponse<Void>> createReservation(SlotReservationRequest request) {
        log.info("Create reservation - roomId: {}, date: {}, time: {}",
                request.roomId(), request.slotDate(), request.slotTime());

        return timeSlotClient.createReservation(request)
                .then(Mono.just(ApiResponse.<Void>ok(null)))
                .doOnSuccess(r -> log.info("Reservation created successfully"))
                .doOnError(error -> log.error("Failed to create reservation: {}", error.getMessage()));
    }

    /**
     * 다중 슬롯 예약 생성.
     */
    public Mono<ApiResponse<MultiSlotReservationResponse>> createMultiSlotReservation(MultiSlotReservationRequest request) {
        log.info("Create multi-slot reservation - roomId: {}, date: {}, times: {}",
                request.roomId(), request.slotDate(), request.slotTimes());

        return timeSlotClient.createMultiSlotReservation(request)
                .map(ApiResponse::ok)
                .doOnSuccess(r -> log.info("Multi-slot reservation created: {}", r.getData().reservationId()))
                .doOnError(error -> log.error("Failed to create multi-slot reservation: {}", error.getMessage()));
    }

    /**
     * 룸 운영정책 설정.
     */
    public Mono<ApiResponse<RoomSetupResponse>> setupRoom(ServerHttpRequest request, RoomOperatingPolicySetupRequest setupRequest) {
        String userId = request.getHeaders().getFirst("X-User-Id");
        String appType = request.getHeaders().getFirst("X-App-Type");

        log.info("Setup room - roomId: {}, userId: {}", setupRequest.roomId(), userId);

        return timeSlotClient.setupRoom(userId, appType, setupRequest)
                .map(ApiResponse::ok)
                .doOnSuccess(r -> log.info("Room setup accepted: {}", r.getData().requestId()))
                .doOnError(error -> log.error("Failed to setup room: {}", error.getMessage()));
    }

    /**
     * 슬롯 생성 상태 조회.
     */
    public Mono<ApiResponse<SlotGenerationStatusResponse>> getSetupStatus(String requestId) {
        log.info("Get setup status - requestId: {}", requestId);

        return timeSlotClient.getSetupStatus(requestId)
                .map(ApiResponse::ok)
                .doOnSuccess(r -> log.info("Setup status: {}", r.getData().status()))
                .doOnError(error -> log.error("Failed to get setup status: {}", error.getMessage()));
    }

    /**
     * 휴무일 설정.
     */
    public Mono<ApiResponse<ClosedDateSetupResponse>> setupClosedDates(ServerHttpRequest request, ClosedDateSetupRequest setupRequest) {
        String userId = request.getHeaders().getFirst("X-User-Id");
        String appType = request.getHeaders().getFirst("X-App-Type");

        log.info("Setup closed dates - roomId: {}, userId: {}", setupRequest.roomId(), userId);

        return timeSlotClient.setupClosedDates(userId, appType, setupRequest)
                .map(ApiResponse::ok)
                .doOnSuccess(r -> log.info("Closed dates setup accepted: {}", r.getData().requestId()))
                .doOnError(error -> log.error("Failed to setup closed dates: {}", error.getMessage()));
    }

    /**
     * 슬롯 보완 생성.
     */
    public Mono<ApiResponse<EnsureSlotsResponse>> ensureSlots(Long roomId, ServerHttpRequest request) {
        String userId = request.getHeaders().getFirst("X-User-Id");
        String appType = request.getHeaders().getFirst("X-App-Type");

        log.info("Ensure slots - roomId: {}, userId: {}", roomId, userId);

        return timeSlotClient.ensureSlots(roomId, userId, appType)
                .map(ApiResponse::ok)
                .doOnSuccess(r -> log.info("Slots ensured: {} generated", r.getData().generatedCount()))
                .doOnError(error -> log.error("Failed to ensure slots: {}", error.getMessage()));
    }

    /**
     * 운영시간 업데이트.
     */
    public Mono<ApiResponse<OperatingHoursUpdateResponse>> updateOperatingHours(ServerHttpRequest request, OperatingHoursUpdateRequest updateRequest) {
        String userId = request.getHeaders().getFirst("X-User-Id");
        String appType = request.getHeaders().getFirst("X-App-Type");

        log.info("Update operating hours - roomId: {}, userId: {}", updateRequest.roomId(), userId);

        return timeSlotClient.updateOperatingHours(userId, appType, updateRequest)
                .map(ApiResponse::ok)
                .doOnSuccess(r -> log.info("Operating hours update accepted: {}", r.getData().requestId()))
                .doOnError(error -> log.error("Failed to update operating hours: {}", error.getMessage()));
    }
}
