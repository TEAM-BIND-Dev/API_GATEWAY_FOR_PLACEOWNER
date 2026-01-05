package com.teambind.springproject.api.timeslot.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 예약 가능 슬롯 조회 응답.
 */
public record AvailableSlotResponse(
        Long slotId,
        Long roomId,
        LocalDate slotDate,
        LocalTime slotTime
) {
}
