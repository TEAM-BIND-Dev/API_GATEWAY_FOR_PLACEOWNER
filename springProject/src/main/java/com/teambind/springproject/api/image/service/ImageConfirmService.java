package com.teambind.springproject.api.image.service;

import com.teambind.springproject.api.image.client.ImageClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageConfirmService {

    private final ImageClient imageClient;

    public Mono<Void> confirmImage(String referenceId, List<String> imageIds) {
        if (imageIds == null || imageIds.isEmpty()) {
            log.debug("No images to confirm for referenceId: {}", referenceId);
            return Mono.empty();
        }

        log.info("Confirming images - referenceId: {}, imageIds: {}", referenceId, imageIds);

        if (imageIds.size() == 1) {
            return imageClient.confirmImage(referenceId, imageIds.get(0));
        }
        return imageClient.confirmImage(referenceId, imageIds);
    }
}
