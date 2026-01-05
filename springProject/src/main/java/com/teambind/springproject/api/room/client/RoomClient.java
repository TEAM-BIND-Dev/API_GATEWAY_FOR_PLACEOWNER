package com.teambind.springproject.api.room.client;

import com.teambind.springproject.api.room.dto.Status;
import com.teambind.springproject.api.room.dto.response.ReservationFieldResponse;
import com.teambind.springproject.api.room.dto.response.RoomDetailResponse;
import com.teambind.springproject.api.room.dto.response.RoomSimpleResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RoomClient {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String APP_TYPE_HEADER = "X-App-Type";

    private final WebClient roomInfoWebClient;

    public RoomClient(@Qualifier("roomInfoWebClient") WebClient roomInfoWebClient) {
        this.roomInfoWebClient = roomInfoWebClient;
    }

    public Mono<Long> createRoom(String userId, String appType, Map<String, Object> requestBody) {
        log.debug("Creating room for userId: {}", userId);

        return roomInfoWebClient.post()
                .uri("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID_HEADER, userId)
                .header(APP_TYPE_HEADER, appType)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Long.class)
                .doOnSuccess(roomId -> log.debug("Room created: {}", roomId))
                .doOnError(error -> log.error("Failed to create room: {}", error.getMessage()));
    }

    public Mono<Long> updateRoom(Long roomId, String userId, String appType, Map<String, Object> requestBody) {
        log.debug("Updating room: {} for userId: {}", roomId, userId);

        return roomInfoWebClient.put()
                .uri("/api/rooms/{roomId}", roomId)
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID_HEADER, userId)
                .header(APP_TYPE_HEADER, appType)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Long.class)
                .doOnSuccess(id -> log.debug("Room updated: {}", id))
                .doOnError(error -> log.error("Failed to update room {}: {}", roomId, error.getMessage()));
    }

    public Mono<RoomDetailResponse> getRoom(Long roomId, String appType) {
        log.debug("Getting room: {}", roomId);

        return roomInfoWebClient.get()
                .uri("/api/rooms/{roomId}", roomId)
                .header(APP_TYPE_HEADER, appType)
                .retrieve()
                .bodyToMono(RoomDetailResponse.class)
                .doOnSuccess(response -> log.debug("Room retrieved: {}", roomId))
                .doOnError(error -> log.error("Failed to get room {}: {}", roomId, error.getMessage()));
    }

    public Mono<List<RoomSimpleResponse>> getRoomsByPlace(Long placeId, String appType) {
        log.debug("Getting rooms for placeId: {}", placeId);

        return roomInfoWebClient.get()
                .uri("/api/rooms/place/{placeId}", placeId)
                .header(APP_TYPE_HEADER, appType)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<RoomSimpleResponse>>() {})
                .doOnSuccess(list -> log.debug("Rooms retrieved for placeId: {}, count: {}", placeId, list != null ? list.size() : 0))
                .doOnError(error -> log.error("Failed to get rooms for placeId {}: {}", placeId, error.getMessage()));
    }

    public Mono<Long> updateRoomStatus(Long roomId, String userId, String appType, Status status) {
        log.debug("Updating room status: {} to {}", roomId, status);

        return roomInfoWebClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/rooms/{roomId}/status")
                        .queryParam("status", status)
                        .build(roomId))
                .header(USER_ID_HEADER, userId)
                .header(APP_TYPE_HEADER, appType)
                .retrieve()
                .bodyToMono(Long.class)
                .doOnSuccess(id -> log.debug("Room status updated: {}", id))
                .doOnError(error -> log.error("Failed to update room status {}: {}", roomId, error.getMessage()));
    }

    public Mono<Void> deleteRoom(Long roomId, String userId, String appType) {
        log.debug("Deleting room: {}", roomId);

        return roomInfoWebClient.delete()
                .uri("/api/rooms/{roomId}", roomId)
                .header(USER_ID_HEADER, userId)
                .header(APP_TYPE_HEADER, appType)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.debug("Room deleted: {}", roomId))
                .doOnError(error -> log.error("Failed to delete room {}: {}", roomId, error.getMessage()));
    }

    public Mono<List<ReservationFieldResponse>> getReservationFields(Long roomId) {
        log.debug("Getting reservation fields for roomId: {}", roomId);

        return roomInfoWebClient.get()
                .uri("/api/rooms/{roomId}/reservation-fields", roomId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ReservationFieldResponse>>() {})
                .doOnSuccess(list -> log.debug("Reservation fields retrieved for roomId: {}", roomId))
                .doOnError(error -> log.error("Failed to get reservation fields for roomId {}: {}", roomId, error.getMessage()));
    }

    public Mono<ReservationFieldResponse> addReservationField(Long roomId, String userId, String appType, Map<String, Object> requestBody) {
        log.debug("Adding reservation field for roomId: {}", roomId);

        return roomInfoWebClient.post()
                .uri("/api/rooms/{roomId}/reservation-fields", roomId)
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID_HEADER, userId)
                .header(APP_TYPE_HEADER, appType)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(ReservationFieldResponse.class)
                .doOnSuccess(response -> log.debug("Reservation field added for roomId: {}", roomId))
                .doOnError(error -> log.error("Failed to add reservation field for roomId {}: {}", roomId, error.getMessage()));
    }

    public Mono<Void> deleteReservationField(Long roomId, Long fieldId, String userId, String appType) {
        log.debug("Deleting reservation field: {} for roomId: {}", fieldId, roomId);

        return roomInfoWebClient.delete()
                .uri("/api/rooms/{roomId}/reservation-fields/{fieldId}", roomId, fieldId)
                .header(USER_ID_HEADER, userId)
                .header(APP_TYPE_HEADER, appType)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.debug("Reservation field deleted: {}", fieldId))
                .doOnError(error -> log.error("Failed to delete reservation field {}: {}", fieldId, error.getMessage()));
    }
}
