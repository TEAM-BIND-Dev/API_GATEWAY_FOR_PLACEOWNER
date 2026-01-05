package com.teambind.springproject.api.timeslot.dto.response;

/**
 * 슬롯 보완 생성 응답.
 */
public record EnsureSlotsResponse(
        Long roomId,
        Integer generatedCount
) {
}
