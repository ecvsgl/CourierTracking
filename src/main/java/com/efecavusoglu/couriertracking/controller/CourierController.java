package com.efecavusoglu.couriertracking.controller;

import com.efecavusoglu.couriertracking.model.CourierLocationUpdateRequest;
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
        if (!CourierLocationUpdateRequest.isValid(courierLocation)) {
            return ResponseEntity.badRequest().build();
        }
        courierService.processLocationUpdate(courierLocation);
        return ResponseEntity.ok().build();
    }

    /**
     * Handles a list of courier location updates.
     * @param courierLocationList A list of courier location data.
     * @return ResponseEntity to indicate request success or failure.
     */
    @PostMapping("/locations")
    public ResponseEntity<Void> updateCourierLocations(@RequestBody List<CourierLocationUpdateRequest> courierLocationList) {
        if (courierLocationList == null || courierLocationList.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        for (CourierLocationUpdateRequest courierLocation : courierLocationList) {
            if (!CourierLocationUpdateRequest.isValid(courierLocation)) {
                courierService.processLocationUpdate(courierLocation);
            }
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Gets the total travel distance for a specific courier.
     * @param courierId The ID of the courier.
     * @return Value of distance traveled by the courier or null if not found.
     */
    @GetMapping("/{courierId}/distance")
    public ResponseEntity<Double> getTotalTravelDistance(@PathVariable String courierId) {
        if (courierId == null || courierId.trim().isEmpty()) {
            return ResponseEntity.badRequest().build(); //
        }
        Double distance = courierService.getTotalTravelDistance(courierId);
        if (distance == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(distance);
    }


}
