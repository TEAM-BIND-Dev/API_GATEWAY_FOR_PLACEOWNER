package com.teambind.springproject.api.place.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teambind.springproject.api.image.service.ImageConfirmService;
import com.teambind.springproject.api.place.client.PlaceClient;
import com.teambind.springproject.api.place.dto.request.PlaceRegisterRequest;
import com.teambind.springproject.api.place.dto.request.PlaceUpdateRequest;
import com.teambind.springproject.api.place.dto.response.PlaceResponse;
import com.teambind.springproject.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceFacadeService {

    private final PlaceClient placeClient;
    private final ImageConfirmService imageConfirmService;
    private final ObjectMapper objectMapper;

    /**
     * 공간 등록 (순차 처리: 등록 성공 후 이미지 확정)
     */
    public Mono<ApiResponse<PlaceResponse>> registerPlace(ServerHttpRequest request, PlaceRegisterRequest placeRegisterRequest) {
        String userId = request.getHeaders().getFirst("X-User-Id");
        String appType = request.getHeaders().getFirst("X-App-Type");

        log.info("Place registration request - userId: {}, appType: {}", userId, appType);

        @SuppressWarnings("unchecked")
        Map<String, Object> requestBody = objectMapper.convertValue(placeRegisterRequest, Map.class);

        return placeClient.registerPlace(userId, appType, requestBody)
                .flatMap(response -> confirmImagesSequential(response, placeRegisterRequest.getImageIds()))
                .map(ApiResponse::ok)
                .doOnSuccess(r -> log.info("Place registered successfully for userId: {}", userId))
                .doOnError(error -> log.error("Failed to register place for userId: {}, error: {}", userId, error.getMessage()));
    }

    /**
     * 내 공간 목록 조회
     */
    public Mono<ApiResponse<List<PlaceResponse>>> getMyPlaces(ServerHttpRequest request) {
        String userId = request.getHeaders().getFirst("X-User-Id");
        String appType = request.getHeaders().getFirst("X-App-Type");

        log.info("Get my places request - userId: {}", userId);

        return placeClient.getMyPlaces(userId, appType)
                .map(ApiResponse::ok)
                .doOnSuccess(r -> log.info("My places retrieved for userId: {}", userId))
                .doOnError(error -> log.error("Failed to get my places for userId: {}, error: {}", userId, error.getMessage()));
    }

    /**
     * 공간 상세 조회
     */
    public Mono<ApiResponse<PlaceResponse>> getPlace(String placeId) {
        log.info("Get place request - placeId: {}", placeId);

        return placeClient.getPlace(placeId)
                .map(ApiResponse::ok)
                .doOnSuccess(r -> log.info("Place retrieved: {}", placeId))
                .doOnError(error -> log.error("Failed to get place {}: {}", placeId, error.getMessage()));
    }

    /**
     * 공간 수정 (병렬 처리: 수정과 이미지 확정 동시 요청)
     */
    public Mono<ApiResponse<PlaceResponse>> updatePlace(String placeId, ServerHttpRequest request, PlaceUpdateRequest updateRequest) {
        String userId = request.getHeaders().getFirst("X-User-Id");
        String appType = request.getHeaders().getFirst("X-App-Type");

        log.info("Place update request - placeId: {}, userId: {}", placeId, userId);

        @SuppressWarnings("unchecked")
        Map<String, Object> requestBody = objectMapper.convertValue(updateRequest, Map.class);

        Mono<PlaceResponse> updateMono = placeClient.updatePlace(placeId, userId, appType, requestBody);

        List<String> imageIds = updateRequest.getImageIds();
        if (imageIds != null && !imageIds.isEmpty()) {
            Mono<Void> imageConfirmMono = imageConfirmService.confirmImage(placeId, imageIds)
                    .doOnError(error -> log.warn("Image confirmation failed for placeId: {}, continuing with update", placeId))
                    .onErrorResume(e -> Mono.empty());

            return Mono.zip(updateMono, imageConfirmMono.then(Mono.just(true)))
                    .map(tuple -> tuple.getT1())
                    .map(ApiResponse::ok)
                    .doOnSuccess(r -> log.info("Place updated with images: {}", placeId))
                    .doOnError(error -> log.error("Failed to update place {}: {}", placeId, error.getMessage()));
        }

        return updateMono
                .map(ApiResponse::ok)
                .doOnSuccess(r -> log.info("Place updated: {}", placeId))
                .doOnError(error -> log.error("Failed to update place {}: {}", placeId, error.getMessage()));
    }

    /**
     * 공간 위치 수정 (단순 프록시)
     */
    public Mono<ApiResponse<Map<String, String>>> updateLocation(String placeId, ServerHttpRequest request, Map<String, Object> requestBody) {
        String userId = request.getHeaders().getFirst("X-User-Id");
        String appType = request.getHeaders().getFirst("X-App-Type");

        log.info("Location update request - placeId: {}, userId: {}", placeId, userId);

        return placeClient.updateLocation(placeId, userId, appType, requestBody)
                .map(ApiResponse::ok)
                .doOnSuccess(r -> log.info("Location updated for placeId: {}", placeId))
                .doOnError(error -> log.error("Failed to update location for placeId {}: {}", placeId, error.getMessage()));
    }

    /**
     * 순차 이미지 확정 (등록 성공 후 처리)
     */
    private Mono<PlaceResponse> confirmImagesSequential(PlaceResponse response, List<String> imageIds) {
        if (imageIds == null || imageIds.isEmpty()) {
            return Mono.just(response);
        }

        String placeId = response.getId();
        log.info("Confirming images after registration - placeId: {}, imageIds: {}", placeId, imageIds);

        return imageConfirmService.confirmImage(placeId, imageIds)
                .doOnSuccess(v -> log.info("Image confirmation success - placeId: {}", placeId))
                .doOnError(error -> log.warn("Image confirmation failed - placeId: {}, error: {}", placeId, error.getMessage()))
                .thenReturn(response)
                .onErrorReturn(response);
    }
}
