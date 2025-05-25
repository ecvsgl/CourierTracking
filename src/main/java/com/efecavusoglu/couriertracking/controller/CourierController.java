package com.efecavusoglu.couriertracking.controller;

import com.efecavusoglu.couriertracking.model.dto.CourierLocationUpdateRequest;
import com.efecavusoglu.couriertracking.service.CourierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courier")
@RequiredArgsConstructor
public class CourierController {

    private final CourierService courierService;

    /**
     * Handles a single courier location update.
     * @param courierLocation The courier location data.
     * @return ResponseEntity to indicate request success or failure.
     */
    @PostMapping("/location")
    public ResponseEntity<Void> updateCourierLocation(@RequestBody CourierLocationUpdateRequest courierLocation) {
        return courierService.processSingleLocationUpdate(courierLocation);
    }

    /**
     * Handles a list of courier location updates.
     * @param courierLocationList A list of courier location data.
     * @return ResponseEntity to indicate request success or failure.
     */
    @PostMapping("/locations")
    public ResponseEntity<Void> updateCourierLocations(@RequestBody List<CourierLocationUpdateRequest> courierLocationList) {
        return courierService.processBatchLocationUpdate(courierLocationList);
    }

    /**
     * Gets the total travel distance for a specific courier.
     * @param courierId The ID of the courier.
     * @return Value of distance traveled by the courier or null if not found.
     */
    @GetMapping("/{courierId}/distance")
    public ResponseEntity<Double> getTotalTravelDistance(@PathVariable String courierId) {
        return courierService.getTotalTravelDistance(courierId);
    }


}
