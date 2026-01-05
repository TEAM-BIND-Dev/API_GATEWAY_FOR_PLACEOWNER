package com.teambind.springproject.api.room.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageInfo {

    private String imageId;
    private String imageUrl;
    private Long sequence;
}
