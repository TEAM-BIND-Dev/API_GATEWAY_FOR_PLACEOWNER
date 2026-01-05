package com.teambind.springproject.api.timeslot.dto.response;

/**
 * 룸 운영정책 설정 응답.
 */
public record RoomSetupResponse(
        String requestId,
        Long roomId,
        String message
) {
}
