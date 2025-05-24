package com.efecavusoglu.couriertracking.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CourierEntry {
    private final CourierLocation courierLocation;
    private final Store store;
    private final double distanceToStoreMeters;
}
