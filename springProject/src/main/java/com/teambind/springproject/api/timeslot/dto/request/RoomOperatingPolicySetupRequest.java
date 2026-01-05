package com.teambind.springproject.api.timeslot.dto.request;

import com.teambind.springproject.api.timeslot.dto.SlotUnit;
import com.teambind.springproject.api.timeslot.dto.WeeklySlotDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 룸 운영 정책 설정 요청.
 */
public record RoomOperatingPolicySetupRequest(
        @NotNull(message = "roomId는 필수입니다")
        Long roomId,

        @NotEmpty(message = "slots는 비어있을 수 없습니다")
        List<WeeklySlotDto> slots,

        @NotNull(message = "slotUnit은 필수입니다")
        SlotUnit slotUnit
) {
}
