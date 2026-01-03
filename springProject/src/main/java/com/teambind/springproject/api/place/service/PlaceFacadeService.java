package com.teambind.springproject.api.place.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teambind.springproject.api.place.client.PlaceClient;
import com.teambind.springproject.api.place.dto.request.PlaceRegisterRequest;
import com.teambind.springproject.api.place.dto.response.PlaceResponse;
import com.teambind.springproject.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceFacadeService {

    private final PlaceClient placeClient;
    private final ObjectMapper objectMapper;

    public Mono<ApiResponse<PlaceResponse>> registerPlace(ServerHttpRequest request, PlaceRegisterRequest placeRegisterRequest) {
        String userId = request.getHeaders().getFirst("X-User-Id");
        String appType = request.getHeaders().getFirst("X-App-Type");

        log.info("Place registration request - userId: {}, appType: {}", userId, appType);

        @SuppressWarnings("unchecked")
        Map<String, Object> requestBody = objectMapper.convertValue(placeRegisterRequest, Map.class);

        return placeClient.registerPlace(userId, appType, requestBody)
                .map(ApiResponse::ok)
                .doOnSuccess(response -> log.info("Place registered successfully for userId: {}", userId))
                .doOnError(error -> log.error("Failed to register place for userId: {}, error: {}", userId, error.getMessage()));
    }
}
