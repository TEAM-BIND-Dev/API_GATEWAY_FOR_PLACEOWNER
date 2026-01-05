package com.teambind.springproject.api.room.dto.response;

import com.teambind.springproject.api.room.dto.FieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationFieldResponse {

    private Long fieldId;
    private String title;
    private FieldType inputType;
    private Boolean required;
    private Integer maxLength;
    private Integer sequence;
}
