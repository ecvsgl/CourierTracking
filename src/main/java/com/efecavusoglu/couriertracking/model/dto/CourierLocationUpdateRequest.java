package com.efecavusoglu.couriertracking.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourierLocationUpdateRequest {
    private String courierId;
    private double latitude;
    private double longitude;
    private LocalDateTime timestamp;

    public static boolean isValid(CourierLocationUpdateRequest courierLocationUpdateRequest) {
        return courierLocationUpdateRequest != null
                && courierLocationUpdateRequest.getCourierId() != null
                && courierLocationUpdateRequest.getTimestamp() != null
                && courierLocationUpdateRequest.getLatitude() != 0.0
                && courierLocationUpdateRequest.getLongitude() != 0.0;
    }
}