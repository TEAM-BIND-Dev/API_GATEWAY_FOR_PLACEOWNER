package com.teambind.springproject.api.timeslot.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

/**
 * 요일별 슬롯 시작 시각 목록.
 */
public record WeeklySlotDto(
        DayOfWeek dayOfWeek,
        RecurrencePattern recurrencePattern,
        List<LocalTime> startTimes
) {
}
