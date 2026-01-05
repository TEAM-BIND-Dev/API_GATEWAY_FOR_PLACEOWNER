package com.teambind.springproject.api.timeslot.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 다중 슬롯 예약 요청.
 */
public record MultiSlotReservationRequest(
        @NotNull(message = "roomId는 필수입니다")
        Long roomId,

        @NotNull(message = "slotDate는 필수입니다")
        LocalDate slotDate,

        @NotEmpty(message = "slotTimes는 비어있을 수 없습니다")
        List<LocalTime> slotTimes
) {
}
