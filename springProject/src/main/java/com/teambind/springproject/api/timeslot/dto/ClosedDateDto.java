package com.teambind.springproject.api.timeslot.dto;

import java.time.LocalDate;

/**
 * 휴무일 정보.
 */
public record ClosedDateDto(
        LocalDate date,
        String reason
) {
}
