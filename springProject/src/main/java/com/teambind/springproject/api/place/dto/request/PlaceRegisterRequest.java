package com.teambind.springproject.api.place.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaceRegisterRequest {

    @NotBlank(message = "장소명은 필수입니다")
    @Size(max = 100, message = "장소명은 100자를 초과할 수 없습니다")
    private String placeName;

    @Size(max = 1000, message = "설명은 1000자를 초과할 수 없습니다")
    private String description;

    @Size(max = 50, message = "카테고리는 50자를 초과할 수 없습니다")
    private String category;

    @Size(max = 50, message = "장소 타입은 50자를 초과할 수 없습니다")
    private String placeType;

    private ContactInfo contact;
    private LocationInfo location;
    private ParkingInfo parking;

    @Getter
    @Builder
    public static class ContactInfo {
        private String contact;
        private String email;
        private List<String> websites;
        private List<String> socialLinks;
    }

    @Getter
    @Builder
    public static class LocationInfo {
        private String from;
        private Map<String, Object> addressData;
        private Double latitude;
        private Double longitude;
        @Size(max = 500, message = "위치 안내는 500자를 초과할 수 없습니다")
        private String locationGuide;
    }

    @Getter
    @Builder
    public static class ParkingInfo {
        private Boolean available;
        private String parkingType;
        private String description;
    }
}
