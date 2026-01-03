package com.teambind.springproject.api.place.controller;

import com.teambind.springproject.api.place.dto.request.PlaceRegisterRequest;
import com.teambind.springproject.api.place.dto.response.PlaceResponse;
import com.teambind.springproject.api.place.service.PlaceFacadeService;
import com.teambind.springproject.api.place.swagger.PlaceControllerSwagger;
import com.teambind.springproject.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
public class PlaceController implements PlaceControllerSwagger {

    private final PlaceFacadeService placeFacadeService;

    @Override
    @PostMapping
    public Mono<ApiResponse<PlaceResponse>> registerPlace(
            ServerHttpRequest request,
            @Valid @RequestBody PlaceRegisterRequest placeRegisterRequest
    ) {
        return placeFacadeService.registerPlace(request, placeRegisterRequest);
    }
}
