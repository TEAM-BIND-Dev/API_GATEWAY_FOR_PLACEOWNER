package com.teambind.springproject.api.timeslot.controller;

import com.teambind.springproject.api.timeslot.dto.request.ClosedDateSetupRequest;
import com.teambind.springproject.api.timeslot.dto.request.MultiSlotReservationRequest;
import com.teambind.springproject.api.timeslot.dto.request.OperatingHoursUpdateRequest;
import com.teambind.springproject.api.timeslot.dto.request.RoomOperatingPolicySetupRequest;
import com.teambind.springproject.api.timeslot.dto.request.SlotReservationRequest;
import com.teambind.springproject.api.timeslot.dto.response.AvailableSlotResponse;
import com.teambind.springproject.api.timeslot.dto.response.ClosedDateSetupResponse;
import com.teambind.springproject.api.timeslot.dto.response.EnsureSlotsResponse;
import com.teambind.springproject.api.timeslot.dto.response.MultiSlotReservationResponse;
import com.teambind.springproject.api.timeslot.dto.response.OperatingHoursUpdateResponse;
import com.teambind.springproject.api.timeslot.dto.response.RoomSetupResponse;
import com.teambind.springproject.api.timeslot.dto.response.SlotGenerationStatusResponse;
import com.teambind.springproject.api.timeslot.service.TimeSlotFacadeService;
import com.teambind.springproject.api.timeslot.swagger.TimeSlotControllerSwagger;
import com.teambind.springproject.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/time-slots")
@RequiredArgsConstructor
public class TimeSlotController implements TimeSlotControllerSwagger {

    private final TimeSlotFacadeService timeSlotFacadeService;

    @Override
    @GetMapping("/available")
    public Mono<ApiResponse<List<AvailableSlotResponse>>> getAvailableSlots(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return timeSlotFacadeService.getAvailableSlots(roomId, date);
    }

    @Override
    @PostMapping("/reserve")
    public Mono<ApiResponse<Void>> createReservation(
            @Valid @RequestBody SlotReservationRequest request
    ) {
        return timeSlotFacadeService.createReservation(request);
    }

    @Override
    @PostMapping("/reserve/multi")
    public Mono<ApiResponse<MultiSlotReservationResponse>> createMultiSlotReservation(
            @Valid @RequestBody MultiSlotReservationRequest request
    ) {
        return timeSlotFacadeService.createMultiSlotReservation(request);
    }

    @Override
    @PostMapping("/setup")
    public Mono<ApiResponse<RoomSetupResponse>> setupRoom(
            ServerHttpRequest request,
            @Valid @RequestBody RoomOperatingPolicySetupRequest setupRequest
    ) {
        return timeSlotFacadeService.setupRoom(request, setupRequest);
    }

    @Override
    @GetMapping("/setup/{requestId}/status")
    public Mono<ApiResponse<SlotGenerationStatusResponse>> getSetupStatus(
            @PathVariable String requestId
    ) {
        return timeSlotFacadeService.getSetupStatus(requestId);
    }

    @Override
    @PostMapping("/setup/closed-dates")
    public Mono<ApiResponse<ClosedDateSetupResponse>> setupClosedDates(
            ServerHttpRequest request,
            @Valid @RequestBody ClosedDateSetupRequest setupRequest
    ) {
        return timeSlotFacadeService.setupClosedDates(request, setupRequest);
    }

    @Override
    @PostMapping("/setup/{roomId}/ensure")
    public Mono<ApiResponse<EnsureSlotsResponse>> ensureSlots(
            @PathVariable Long roomId,
            ServerHttpRequest request
    ) {
        return timeSlotFacadeService.ensureSlots(roomId, request);
    }

    @Override
    @PutMapping("/setup/operating-hours")
    public Mono<ApiResponse<OperatingHoursUpdateResponse>> updateOperatingHours(
            ServerHttpRequest request,
            @Valid @RequestBody OperatingHoursUpdateRequest updateRequest
    ) {
        return timeSlotFacadeService.updateOperatingHours(request, updateRequest);
    }
}
