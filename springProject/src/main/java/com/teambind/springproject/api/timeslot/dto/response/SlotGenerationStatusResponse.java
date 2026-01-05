package com.teambind.springproject.api.timeslot.dto.response;

/**
 * 슬롯 생성 상태 조회 응답.
 */
public record SlotGenerationStatusResponse(
        String requestId,
        String status,
        String message,
        Integer generatedCount
) {
}
