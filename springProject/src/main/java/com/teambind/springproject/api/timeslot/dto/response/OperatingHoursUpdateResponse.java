package com.teambind.springproject.api.timeslot.dto.response;

/**
 * 운영시간 업데이트 응답.
 */
public record OperatingHoursUpdateResponse(
        String requestId,
        Long roomId,
        String message
) {
}
