package com.teambind.springproject.api.room.dto.response;

import com.teambind.springproject.api.room.dto.Status;
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
public class RoomDetailResponse {

    private Long roomId;
    private String roomName;
    private Long placeId;
    private Status status;
    private TimeSlot timeSlot;
    private Integer maxOccupancy;
    private List<String> furtherDetails;
    private List<String> cautionDetails;
    private List<ImageInfo> images;
    private List<Long> keywordIds;
}
