package com.teambind.springproject.api.room.dto.response;

import com.teambind.springproject.api.room.dto.TimeSlot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomSimpleResponse {

    private Long roomId;
    private String roomName;
    private Long placeId;
    private TimeSlot timeSlot;
    private Integer maxOccupancy;
    private List<ImageInfo> images;
    private List<Long> keywordIds;
}
