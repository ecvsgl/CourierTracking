package com.efecavusoglu.couriertracking.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreDTO {
    private String name;
    private double lat;
    private double lng;
}
