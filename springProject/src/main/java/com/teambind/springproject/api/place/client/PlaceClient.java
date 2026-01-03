package com.teambind.springproject.api.place.client;

import com.teambind.springproject.api.place.dto.response.PlaceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
}
