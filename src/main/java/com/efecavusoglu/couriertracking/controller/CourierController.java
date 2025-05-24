package com.efecavusoglu.couriertracking.controller;

import com.efecavusoglu.couriertracking.model.CourierLocation;
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
     * Receives a single courier location update.
     * @param courierLocation The courier location data.
     * @return ResponseEntity indicating success.
     */
    @PostMapping("/location")
    public ResponseEntity<Void> updateCourierLocation(@RequestBody CourierLocation courierLocation) {
        // Basic validation, more can be added
        if (courierLocation == null || courierLocation.getCourierId() == null || courierLocation.getTimestamp() == null) {
            return ResponseEntity.badRequest().build();
        }
        //courierService.processLocationUpdate(courierLocation);
        return ResponseEntity.ok().build();
    }

    /**
     * Receives a batch of courier location updates.
     * @param courierLocationList A list of courier location data.
     * @return ResponseEntity indicating success or partial success.
     */
    @PostMapping("/locations")
    public ResponseEntity<Void> updateCourierLocations(@RequestBody List<CourierLocation> courierLocationList) {
        if (courierLocationList == null || courierLocationList.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        for (CourierLocation location : courierLocationList) {
            if (location != null && location.getCourierId() != null && location.getTimestamp() != null) {
                //courierService.processLocationUpdate(location);
            }
        }
        return ResponseEntity.ok().build();
    }

//    /**
//     * Gets the total travel distance for a specific courier.
//     * @param courierId The ID of the courier.
//     * @return A map containing the courierId and their total travel distance in kilometers.
//     */
//    @GetMapping("/{courierId}/distance")
//    public ResponseEntity<Double> getTotalTravelDistance(@PathVariable String courierId) {
//        if (courierId == null || courierId.trim().isEmpty()) {
//            return ResponseEntity.badRequest().build(); // Or consider if this path is even reachable with Spring's @PathVariable
//        }
//        Double distance = courierService.getTotalTravelDistance(courierId);
//        if (distance == null) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(distance);
//    }


}
