package com.teambind.springproject.api.timeslot.dto.request;

import com.teambind.springproject.api.timeslot.dto.ClosedDateDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 휴무일 설정 요청.
 */
public record ClosedDateSetupRequest(
        @NotNull(message = "roomId는 필수입니다")
        Long roomId,

        @NotEmpty(message = "closedDates는 비어있을 수 없습니다")
        List<ClosedDateDto> closedDates
) {
}
