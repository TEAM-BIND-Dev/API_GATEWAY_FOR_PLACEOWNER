package com.teambind.springproject.api.room.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teambind.springproject.api.image.service.ImageConfirmService;
import com.teambind.springproject.api.room.client.RoomClient;
import com.teambind.springproject.api.room.dto.Status;
import com.teambind.springproject.api.room.dto.request.RoomCreateRequest;
import com.teambind.springproject.api.room.dto.request.RoomUpdateRequest;
import com.teambind.springproject.api.room.dto.response.ReservationFieldResponse;
import com.teambind.springproject.api.room.dto.response.RoomDetailResponse;
import com.teambind.springproject.api.room.dto.response.RoomSimpleResponse;
import com.teambind.springproject.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomFacadeService {

    private final RoomClient roomClient;
    private final ImageConfirmService imageConfirmService;
    private final ObjectMapper objectMapper;

    /**
     * Room 등록 (순차 처리: 등록 성공 후 이미지 확정)
     */
    public Mono<ApiResponse<Long>> createRoom(ServerHttpRequest request, RoomCreateRequest createRequest) {
        String userId = request.getHeaders().getFirst("X-User-Id");
        String appType = request.getHeaders().getFirst("X-App-Type");

        log.info("Room creation request - userId: {}, placeId: {}", userId, createRequest.getPlaceId());

        Map<String, Object> requestBody = convertToMapWithoutImageIds(createRequest);
        List<String> imageIds = createRequest.getImageIds();

        return roomClient.createRoom(userId, appType, requestBody)
                .flatMap(roomId -> confirmImagesSequential(roomId, imageIds))
                .map(ApiResponse::ok)
                .doOnSuccess(r -> log.info("Room created successfully for userId: {}", userId))
                .doOnError(error -> log.error("Failed to create room for userId: {}, error: {}", userId, error.getMessage()));
    }

    /**
     * Room 수정 (병렬 처리: 수정과 이미지 확정 동시 요청)
     */
    public Mono<ApiResponse<Long>> updateRoom(Long roomId, ServerHttpRequest request, RoomUpdateRequest updateRequest) {
        String userId = request.getHeaders().getFirst("X-User-Id");
        String appType = request.getHeaders().getFirst("X-App-Type");

        log.info("Room update request - roomId: {}, userId: {}", roomId, userId);

        Map<String, Object> requestBody = convertToMapWithoutImageIds(updateRequest);
        List<String> imageIds = updateRequest.getImageIds();

        Mono<Long> updateMono = roomClient.updateRoom(roomId, userId, appType, requestBody);

        if (imageIds != null && !imageIds.isEmpty()) {
            Mono<Void> imageConfirmMono = imageConfirmService.confirmImage(String.valueOf(roomId), imageIds)
                    .doOnError(error -> log.warn("Image confirmation failed for roomId: {}, continuing with update", roomId))
                    .onErrorResume(e -> Mono.empty());

            return Mono.zip(updateMono, imageConfirmMono.then(Mono.just(true)))
                    .map(tuple -> tuple.getT1())
                    .map(ApiResponse::ok)
                    .doOnSuccess(r -> log.info("Room updated with images: {}", roomId))
                    .doOnError(error -> log.error("Failed to update room {}: {}", roomId, error.getMessage()));
        }

        return updateMono
                .map(ApiResponse::ok)
                .doOnSuccess(r -> log.info("Room updated: {}", roomId))
                .doOnError(error -> log.error("Failed to update room {}: {}", roomId, error.getMessage()));
    }

    /**
     * Room 상세 조회
     */
    public Mono<ApiResponse<RoomDetailResponse>> getRoom(Long roomId, ServerHttpRequest request) {
        String appType = request.getHeaders().getFirst("X-App-Type");

        log.info("Get room request - roomId: {}", roomId);

        return roomClient.getRoom(roomId, appType)
                .map(ApiResponse::ok)
                .doOnSuccess(r -> log.info("Room retrieved: {}", roomId))
                .doOnError(error -> log.error("Failed to get room {}: {}", roomId, error.getMessage()));
    }

    /**
     * Place별 Room 목록 조회
     */
    public Mono<ApiResponse<List<RoomSimpleResponse>>> getRoomsByPlace(Long placeId, ServerHttpRequest request) {
        String appType = request.getHeaders().getFirst("X-App-Type");

        log.info("Get rooms by place request - placeId: {}", placeId);

        return roomClient.getRoomsByPlace(placeId, appType)
                .map(ApiResponse::ok)
                .doOnSuccess(r -> log.info("Rooms retrieved for placeId: {}", placeId))
                .doOnError(error -> log.error("Failed to get rooms for placeId {}: {}", placeId, error.getMessage()));
    }

    /**
     * Room 상태 변경
     */
    public Mono<ApiResponse<Long>> updateRoomStatus(Long roomId, Status status, ServerHttpRequest request) {
        String userId = request.getHeaders().getFirst("X-User-Id");
        String appType = request.getHeaders().getFirst("X-App-Type");

        log.info("Update room status request - roomId: {}, status: {}", roomId, status);

        return roomClient.updateRoomStatus(roomId, userId, appType, status)
                .map(ApiResponse::ok)
                .doOnSuccess(r -> log.info("Room status updated: {} to {}", roomId, status))
                .doOnError(error -> log.error("Failed to update room status {}: {}", roomId, error.getMessage()));
    }

    /**
     * Room 삭제
     */
    public Mono<ApiResponse<Void>> deleteRoom(Long roomId, ServerHttpRequest request) {
        String userId = request.getHeaders().getFirst("X-User-Id");
        String appType = request.getHeaders().getFirst("X-App-Type");

        log.info("Delete room request - roomId: {}", roomId);

        return roomClient.deleteRoom(roomId, userId, appType)
                .then(Mono.just(ApiResponse.<Void>ok(null)))
                .doOnSuccess(r -> log.info("Room deleted: {}", roomId))
                .doOnError(error -> log.error("Failed to delete room {}: {}", roomId, error.getMessage()));
    }

    /**
     * 예약 필드 조회
     */
    public Mono<ApiResponse<List<ReservationFieldResponse>>> getReservationFields(Long roomId) {
        log.info("Get reservation fields request - roomId: {}", roomId);

        return roomClient.getReservationFields(roomId)
                .map(ApiResponse::ok)
                .doOnSuccess(r -> log.info("Reservation fields retrieved for roomId: {}", roomId))
                .doOnError(error -> log.error("Failed to get reservation fields for roomId {}: {}", roomId, error.getMessage()));
    }

    /**
     * 예약 필드 추가
     */
    public Mono<ApiResponse<ReservationFieldResponse>> addReservationField(Long roomId, ServerHttpRequest request, Map<String, Object> requestBody) {
        String userId = request.getHeaders().getFirst("X-User-Id");
        String appType = request.getHeaders().getFirst("X-App-Type");

        log.info("Add reservation field request - roomId: {}", roomId);

        return roomClient.addReservationField(roomId, userId, appType, requestBody)
                .map(ApiResponse::ok)
                .doOnSuccess(r -> log.info("Reservation field added for roomId: {}", roomId))
                .doOnError(error -> log.error("Failed to add reservation field for roomId {}: {}", roomId, error.getMessage()));
    }

    /**
     * 예약 필드 삭제
     */
    public Mono<ApiResponse<Void>> deleteReservationField(Long roomId, Long fieldId, ServerHttpRequest request) {
        String userId = request.getHeaders().getFirst("X-User-Id");
        String appType = request.getHeaders().getFirst("X-App-Type");

        log.info("Delete reservation field request - roomId: {}, fieldId: {}", roomId, fieldId);

        return roomClient.deleteReservationField(roomId, fieldId, userId, appType)
                .then(Mono.just(ApiResponse.<Void>ok(null)))
                .doOnSuccess(r -> log.info("Reservation field deleted: {}", fieldId))
                .doOnError(error -> log.error("Failed to delete reservation field {}: {}", fieldId, error.getMessage()));
    }

    /**
     * 순차 이미지 확정 (등록 성공 후 처리)
     */
    private Mono<Long> confirmImagesSequential(Long roomId, List<String> imageIds) {
        if (imageIds == null || imageIds.isEmpty()) {
            return Mono.just(roomId);
        }

        log.info("Confirming images after room creation - roomId: {}, imageIds: {}", roomId, imageIds);

        return imageConfirmService.confirmImage(String.valueOf(roomId), imageIds)
                .doOnSuccess(v -> log.info("Image confirmation success - roomId: {}", roomId))
                .doOnError(error -> log.warn("Image confirmation failed - roomId: {}, error: {}", roomId, error.getMessage()))
                .thenReturn(roomId)
                .onErrorReturn(roomId);
    }

    /**
     * DTO를 Map으로 변환 (imageIds 제외)
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToMapWithoutImageIds(Object dto) {
        Map<String, Object> map = objectMapper.convertValue(dto, Map.class);
        map.remove("imageIds");
        return new HashMap<>(map);
    }
}
