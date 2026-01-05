package com.teambind.springproject.api.place.controller;

import com.teambind.springproject.api.place.dto.request.PlaceRegisterRequest;
import com.teambind.springproject.api.place.dto.request.PlaceUpdateRequest;
import com.teambind.springproject.api.place.dto.response.PlaceResponse;
import com.teambind.springproject.api.place.service.PlaceFacadeService;
import com.teambind.springproject.api.place.swagger.PlaceControllerSwagger;
import com.teambind.springproject.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

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

    @Override
    @GetMapping("/my")
    public Mono<ApiResponse<List<PlaceResponse>>> getMyPlaces(ServerHttpRequest request) {
        return placeFacadeService.getMyPlaces(request);
    }

    @Override
    @GetMapping("/{placeId}")
    public Mono<ApiResponse<PlaceResponse>> getPlace(@PathVariable String placeId) {
        return placeFacadeService.getPlace(placeId);
    }

    @Override
    @PutMapping("/{placeId}")
    public Mono<ApiResponse<PlaceResponse>> updatePlace(
            @PathVariable String placeId,
            ServerHttpRequest request,
            @Valid @RequestBody PlaceUpdateRequest updateRequest
    ) {
        return placeFacadeService.updatePlace(placeId, request, updateRequest);
    }
}
