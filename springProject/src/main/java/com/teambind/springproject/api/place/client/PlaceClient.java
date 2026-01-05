package com.teambind.springproject.api.place.client;

import com.teambind.springproject.api.place.dto.response.PlaceResponse;
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
public class PlaceClient {

    private final WebClient placeInfoWebClient;

    public PlaceClient(@Qualifier("placeInfoWebClient") WebClient placeInfoWebClient) {
        this.placeInfoWebClient = placeInfoWebClient;
    }

    public Mono<PlaceResponse> registerPlace(String userId, String appType, Map<String, Object> requestBody) {
        log.debug("Registering place for userId: {}", userId);

        return placeInfoWebClient.post()
                .uri("/api/v1/places")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-User-Id", userId)
                .header("X-App-Type", appType)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(PlaceResponse.class)
                .doOnSuccess(response -> log.debug("Place registered successfully: {}", response.getId()))
                .doOnError(error -> log.error("Failed to register place: {}", error.getMessage()));
    }

    public Mono<List<PlaceResponse>> getMyPlaces(String userId, String appType) {
        log.debug("Getting my places for userId: {}", userId);

        return placeInfoWebClient.get()
                .uri("/api/v1/places/my")
                .header("X-User-Id", userId)
                .header("X-App-Type", appType)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<PlaceResponse>>() {})
                .doOnSuccess(response -> log.debug("My places retrieved, count: {}", response != null ? response.size() : 0))
                .doOnError(error -> log.error("Failed to get my places: {}", error.getMessage()));
    }

    public Mono<PlaceResponse> getPlace(String placeId) {
        log.debug("Getting place: {}", placeId);

        return placeInfoWebClient.get()
                .uri("/api/v1/places/{placeId}", placeId)
                .retrieve()
                .bodyToMono(PlaceResponse.class)
                .doOnSuccess(response -> log.debug("Place retrieved: {}", placeId))
                .doOnError(error -> log.error("Failed to get place {}: {}", placeId, error.getMessage()));
    }

    public Mono<PlaceResponse> updatePlace(String placeId, String userId, String appType, Map<String, Object> requestBody) {
        log.debug("Updating place: {} for userId: {}", placeId, userId);

        return placeInfoWebClient.put()
                .uri("/api/v1/places/{placeId}", placeId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-User-Id", userId)
                .header("X-App-Type", appType)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(PlaceResponse.class)
                .doOnSuccess(response -> log.debug("Place updated: {}", placeId))
                .doOnError(error -> log.error("Failed to update place {}: {}", placeId, error.getMessage()));
    }
}
