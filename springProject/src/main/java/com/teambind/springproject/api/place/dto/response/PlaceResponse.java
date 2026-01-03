package com.teambind.springproject.api.place.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaceResponse {

    private String id;
    private String userId;
    private String placeName;
    private String description;
    private String category;
    private String placeType;
    private ContactInfo contact;
    private LocationInfo location;
    private ParkingInfo parking;
    private List<String> images;
    private List<String> imageUrls;
    private List<String> keywords;
    private Boolean isActive;
    private String approvalStatus;
    private String registrationStatus;
    private Double ratingAverage;
    private Integer reviewCount;
    private Integer roomCount;
    private List<String> roomIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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
        private AddressInfo address;
        private Double latitude;
        private Double longitude;
        private String locationGuide;
    }

    @Getter
    @Builder
    public static class AddressInfo {
        private String province;
        private String city;
        private String district;
        private String fullAddress;
        private String addressDetail;
        private String postalCode;
        private String shortAddress;
    }

    @Getter
    @Builder
    public static class ParkingInfo {
        private Boolean available;
        private String parkingType;
        private String description;
    }
}
