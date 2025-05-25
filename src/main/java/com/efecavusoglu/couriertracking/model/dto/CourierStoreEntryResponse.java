package com.efecavusoglu.couriertracking.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourierStoreEntryResponse {
    private String courierId;
    private String storeName;
    private LocalDateTime timestamp;
}
