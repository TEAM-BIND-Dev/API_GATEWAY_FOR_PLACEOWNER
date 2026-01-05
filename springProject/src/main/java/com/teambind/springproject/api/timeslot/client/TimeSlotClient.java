package com.teambind.springproject.api.timeslot.client;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
public class TimeSlotClient {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String APP_TYPE_HEADER = "X-App-Type";

    private final WebClient webClient;

    public TimeSlotClient(@Qualifier("leeYongGwanLeeWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * 예약 가능한 슬롯 목록 조회.
     */
    public Mono<List<AvailableSlotResponse>> getAvailableSlots(Long roomId, LocalDate date) {
        log.debug("Getting available slots - roomId: {}, date: {}", roomId, date);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/reservations/available-slots")
                        .queryParam("roomId", roomId)
                        .queryParam("date", date)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<AvailableSlotResponse>>() {})
                .doOnSuccess(list -> log.debug("Available slots retrieved: {}", list != null ? list.size() : 0))
                .doOnError(error -> log.error("Failed to get available slots: {}", error.getMessage()));
    }

    /**
     * 단일 슬롯 예약 생성.
     */
    public Mono<Void> createReservation(SlotReservationRequest request) {
        log.debug("Creating reservation - roomId: {}, date: {}, time: {}",
                request.roomId(), request.slotDate(), request.slotTime());

        return webClient.post()
                .uri("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.debug("Reservation created successfully"))
                .doOnError(error -> log.error("Failed to create reservation: {}", error.getMessage()));
    }

    /**
     * 다중 슬롯 예약 생성.
     */
    public Mono<MultiSlotReservationResponse> createMultiSlotReservation(MultiSlotReservationRequest request) {
        log.debug("Creating multi-slot reservation - roomId: {}, date: {}, times: {}",
                request.roomId(), request.slotDate(), request.slotTimes());

        return webClient.post()
                .uri("/api/v1/reservations/multi")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(MultiSlotReservationResponse.class)
                .doOnSuccess(response -> log.debug("Multi-slot reservation created: {}", response.reservationId()))
                .doOnError(error -> log.error("Failed to create multi-slot reservation: {}", error.getMessage()));
    }

    /**
     * 룸 운영 정책 설정.
     */
    public Mono<RoomSetupResponse> setupRoom(String userId, String appType, RoomOperatingPolicySetupRequest request) {
        log.debug("Setting up room - roomId: {}", request.roomId());

        return webClient.post()
                .uri("/api/rooms/setup")
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID_HEADER, userId)
                .header(APP_TYPE_HEADER, appType)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(RoomSetupResponse.class)
                .doOnSuccess(response -> log.debug("Room setup accepted: {}", response.requestId()))
                .doOnError(error -> log.error("Failed to setup room: {}", error.getMessage()));
    }

    /**
     * 슬롯 생성 상태 조회.
     */
    public Mono<SlotGenerationStatusResponse> getSetupStatus(String requestId) {
        log.debug("Getting setup status - requestId: {}", requestId);

        return webClient.get()
                .uri("/api/rooms/setup/{requestId}/status", requestId)
                .retrieve()
                .bodyToMono(SlotGenerationStatusResponse.class)
                .doOnSuccess(response -> log.debug("Setup status: {}", response.status()))
                .doOnError(error -> log.error("Failed to get setup status: {}", error.getMessage()));
    }

    /**
     * 휴무일 설정.
     */
    public Mono<ClosedDateSetupResponse> setupClosedDates(String userId, String appType, ClosedDateSetupRequest request) {
        log.debug("Setting up closed dates - roomId: {}", request.roomId());

        return webClient.post()
                .uri("/api/rooms/setup/closed-dates")
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID_HEADER, userId)
                .header(APP_TYPE_HEADER, appType)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ClosedDateSetupResponse.class)
                .doOnSuccess(response -> log.debug("Closed dates setup accepted: {}", response.requestId()))
                .doOnError(error -> log.error("Failed to setup closed dates: {}", error.getMessage()));
    }

    /**
     * 슬롯 보완 생성.
     */
    public Mono<EnsureSlotsResponse> ensureSlots(Long roomId, String userId, String appType) {
        log.debug("Ensuring slots - roomId: {}", roomId);

        return webClient.post()
                .uri("/api/rooms/setup/{roomId}/ensure-slots", roomId)
                .header(USER_ID_HEADER, userId)
                .header(APP_TYPE_HEADER, appType)
                .retrieve()
                .bodyToMono(EnsureSlotsResponse.class)
                .doOnSuccess(response -> log.debug("Slots ensured: {} generated", response.generatedCount()))
                .doOnError(error -> log.error("Failed to ensure slots: {}", error.getMessage()));
    }

    /**
     * 운영 시간 업데이트.
     */
    public Mono<OperatingHoursUpdateResponse> updateOperatingHours(String userId, String appType, OperatingHoursUpdateRequest request) {
        log.debug("Updating operating hours - roomId: {}", request.roomId());

        return webClient.put()
                .uri("/api/rooms/setup/operating-hours")
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID_HEADER, userId)
                .header(APP_TYPE_HEADER, appType)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OperatingHoursUpdateResponse.class)
                .doOnSuccess(response -> log.debug("Operating hours update accepted: {}", response.requestId()))
                .doOnError(error -> log.error("Failed to update operating hours: {}", error.getMessage()));
    }
}
