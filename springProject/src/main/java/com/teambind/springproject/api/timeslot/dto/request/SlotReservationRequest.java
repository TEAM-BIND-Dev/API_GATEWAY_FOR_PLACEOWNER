package com.teambind.springproject.api.timeslot.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 단일 슬롯 예약 요청.
 */
public record SlotReservationRequest(
        @NotNull(message = "roomId는 필수입니다")
        Long roomId,

        @NotNull(message = "slotDate는 필수입니다")
        LocalDate slotDate,

        @NotNull(message = "slotTime은 필수입니다")
        LocalTime slotTime,

        Long reservationId
) {
}
