package com.efecavusoglu.couriertracking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourierLocation {
    private String courierId;
    private double latitude;
    private double longitude;
    private LocalDateTime timestamp;
}