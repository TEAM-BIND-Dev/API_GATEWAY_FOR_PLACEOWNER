package com.teambind.springproject.api.timeslot.dto.request;

import com.teambind.springproject.api.timeslot.dto.SlotUnit;
import com.teambind.springproject.api.timeslot.dto.WeeklySlotDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 운영 시간 업데이트 요청.
 */
public record OperatingHoursUpdateRequest(
        @NotNull(message = "roomId는 필수입니다")
        Long roomId,

        @NotEmpty(message = "slots는 비어있을 수 없습니다")
        List<WeeklySlotDto> slots,

        @NotNull(message = "slotUnit은 필수입니다")
        SlotUnit slotUnit
) {
}
