package com.teambind.springproject.api.image.client;

import com.teambind.springproject.api.image.dto.request.ImageConfirmRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class ImageClient {

    private final WebClient imageWebClient;

    public ImageClient(@Qualifier("imageWebClient") WebClient imageWebClient) {
        this.imageWebClient = imageWebClient;
    }

    public Mono<Void> confirmImage(String referenceId, String imageId) {
        String uri = UriComponentsBuilder.fromPath("/api/v1/images/confirm/" + referenceId)
                .queryParam("imageId", imageId)
                .toUriString();

        log.info("Confirming single image - referenceId: {}, imageId: {}", referenceId, imageId);

        return imageWebClient.post()
                .uri(uri)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("Image confirmation success - referenceId: {}, imageId: {}", referenceId, imageId))
                .doOnError(error -> log.error("Image confirmation failed - referenceId: {}, imageId: {}, error: {}",
                        referenceId, imageId, error.getMessage()));
    }

    public Mono<Void> confirmImage(String referenceId, List<String> imageIds) {
        String uri = UriComponentsBuilder.fromPath("/api/v1/images/confirm").toUriString();

        ImageConfirmRequest request = ImageConfirmRequest.builder()
                .referenceId(referenceId)
                .imageIds(imageIds)
                .build();

        log.info("Confirming batch images - referenceId: {}, imageIds: {}", referenceId, imageIds);

        return imageWebClient.post()
                .uri(uri)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("Batch image confirmation success - referenceId: {}, imageIds: {}", referenceId, imageIds))
                .doOnError(error -> log.error("Batch image confirmation failed - referenceId: {}, imageIds: {}, error: {}",
                        referenceId, imageIds, error.getMessage()));
    }
}
