package com.teambind.springproject.api.image.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageConfirmRequest {

    private String referenceId;
    private List<String> imageIds;
}
