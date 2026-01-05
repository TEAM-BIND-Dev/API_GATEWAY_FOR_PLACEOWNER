package com.teambind.springproject.api.room.controller;

import com.teambind.springproject.api.room.dto.Status;
import com.teambind.springproject.api.room.dto.request.RoomCreateRequest;
import com.teambind.springproject.api.room.dto.request.RoomUpdateRequest;
import com.teambind.springproject.api.room.dto.response.ReservationFieldResponse;
import com.teambind.springproject.api.room.dto.response.RoomDetailResponse;
import com.teambind.springproject.api.room.dto.response.RoomSimpleResponse;
import com.teambind.springproject.api.room.service.RoomFacadeService;
import com.teambind.springproject.api.room.swagger.RoomControllerSwagger;
import com.teambind.springproject.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController implements RoomControllerSwagger {

    private final RoomFacadeService roomFacadeService;

    @Override
    @PostMapping
    public Mono<ApiResponse<Long>> createRoom(
            ServerHttpRequest request,
            @Valid @RequestBody RoomCreateRequest createRequest
    ) {
        return roomFacadeService.createRoom(request, createRequest);
    }

    @Override
    @PutMapping("/{roomId}")
    public Mono<ApiResponse<Long>> updateRoom(
            @PathVariable Long roomId,
            ServerHttpRequest request,
            @Valid @RequestBody RoomUpdateRequest updateRequest
    ) {
        return roomFacadeService.updateRoom(roomId, request, updateRequest);
    }

    @Override
    @GetMapping("/{roomId}")
    public Mono<ApiResponse<RoomDetailResponse>> getRoom(
            @PathVariable Long roomId,
            ServerHttpRequest request
    ) {
        return roomFacadeService.getRoom(roomId, request);
    }

    @Override
    @GetMapping("/place/{placeId}")
    public Mono<ApiResponse<List<RoomSimpleResponse>>> getRoomsByPlace(
            @PathVariable Long placeId,
            ServerHttpRequest request
    ) {
        return roomFacadeService.getRoomsByPlace(placeId, request);
    }

    @Override
    @PatchMapping("/{roomId}/status")
    public Mono<ApiResponse<Long>> updateRoomStatus(
            @PathVariable Long roomId,
            @RequestParam Status status,
            ServerHttpRequest request
    ) {
        return roomFacadeService.updateRoomStatus(roomId, status, request);
    }

    @Override
    @DeleteMapping("/{roomId}")
    public Mono<ApiResponse<Void>> deleteRoom(
            @PathVariable Long roomId,
            ServerHttpRequest request
    ) {
        return roomFacadeService.deleteRoom(roomId, request);
    }

    @Override
    @GetMapping("/{roomId}/reservation-fields")
    public Mono<ApiResponse<List<ReservationFieldResponse>>> getReservationFields(
            @PathVariable Long roomId
    ) {
        return roomFacadeService.getReservationFields(roomId);
    }

    @Override
    @PostMapping("/{roomId}/reservation-fields")
    public Mono<ApiResponse<ReservationFieldResponse>> addReservationField(
            @PathVariable Long roomId,
            ServerHttpRequest request,
            @RequestBody Map<String, Object> requestBody
    ) {
        return roomFacadeService.addReservationField(roomId, request, requestBody);
    }

    @Override
    @DeleteMapping("/{roomId}/reservation-fields/{fieldId}")
    public Mono<ApiResponse<Void>> deleteReservationField(
            @PathVariable Long roomId,
            @PathVariable Long fieldId,
            ServerHttpRequest request
    ) {
        return roomFacadeService.deleteReservationField(roomId, fieldId, request);
    }
}
