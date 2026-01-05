package com.teambind.springproject.api.timeslot.dto.response;

/**
 * 휴무일 설정 응답.
 */
public record ClosedDateSetupResponse(
        String requestId,
        Long roomId,
        String message
) {
}
