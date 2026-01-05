package com.teambind.springproject.api.timeslot.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 다중 슬롯 예약 응답.
 */
public record MultiSlotReservationResponse(
        Long reservationId,
        Long roomId,
        LocalDate slotDate,
        List<LocalTime> reservedSlotTimes
) {
}
