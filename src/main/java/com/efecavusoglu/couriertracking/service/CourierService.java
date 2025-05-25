package com.efecavusoglu.couriertracking.service;

import com.efecavusoglu.couriertracking.exception.InsufficientDataException;
import com.efecavusoglu.couriertracking.model.dto.CourierLocationUpdateRequest;
import com.efecavusoglu.couriertracking.model.entity.CourierLocationEntity;
import com.efecavusoglu.couriertracking.model.entity.StoreEntity;
import com.efecavusoglu.couriertracking.repository.CourierLocationRepository;
import com.efecavusoglu.couriertracking.repository.CourierStoreEntryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.efecavusoglu.couriertracking.util.DistanceUtil.calculateDistance;

@Service
public class CourierService {

    private static final double STORE_PROXIMITY_RADIUS_METERS = 100;
    private static final long REENTRY_COOLDOWN_MINUTES = 1;

    private final StoreService storeService;
    private final CourierLocationRepository courierLocationRepository;
    private final CourierStoreEntryRepository courierStoreEntryRepository;

    public CourierService(StoreService storeService, CourierLocationRepository courierLocationRepository, CourierStoreEntryRepository courierStoreEntryRepository) {
        this.storeService = storeService;
        this.courierLocationRepository = courierLocationRepository;
        this.courierStoreEntryRepository = courierStoreEntryRepository;
    }

    /**
     * Han
     * @param courierLocationUpdateRequest
     */
    public ResponseEntity<Void> processSingleLocationUpdate(CourierLocationUpdateRequest courierLocationUpdateRequest) {
        CourierLocationEntity preSaveEntity = mapLocationUpdateRequestToLocationEntity(courierLocationUpdateRequest);
        CourierLocationEntity courierLocationEntity = courierLocationRepository.save(preSaveEntity);
        evaluateIfStoreEntryTriggered(courierLocationEntity);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> processBatchLocationUpdate(List<CourierLocationUpdateRequest> courierLocationList) {
        if (courierLocationList == null || courierLocationList.isEmpty()) {
            throw new IllegalArgumentException("Please provide at least one location update request.");
        }

        evaluateIfStoreEntryTriggered(courierLocationList.stream()
                .filter(CourierLocationUpdateRequest::isValid)
                .map(this::mapLocationUpdateRequestToLocationEntity)
                .toList());

        return ResponseEntity.ok().build();
    }




    private void evaluateIfStoreEntryTriggered(CourierLocationEntity courierLocationEntity) {

    }

    private void evaluateIfStoreEntryTriggered(List<CourierLocationEntity> courierLocationEntityList) {

    }

    /**
     * Calculate the total distance traveled by a courier from the start of its first location to the last location.
     * @param courierId courier ID to be checked.
     * @return A response entity with total distance traveled by a courier, returns null if courierId is not found.
     */
    public ResponseEntity<Double> getTotalTravelDistance(String courierId) {
        if (courierId == null || courierId.trim().isEmpty()) {
            throw new IllegalArgumentException("Courier ID cannot be null or empty.");
        }

        List<CourierLocationEntity> courierLocations = courierLocationRepository.findByCourierIdOrderByTimestampAsc(courierId);

        if (courierLocations == null || courierLocations.size() < 2) {
            throw new InsufficientDataException("Courier ID " + courierId + " has less than 2 locations. Please provide at least 2 locations to calculate distance.");
        }

        double distance = 0.0;
        for (int i = 0; i < courierLocations.size() - 1; i++) {
            CourierLocationEntity courierLocation = courierLocations.get(i);
            CourierLocationEntity nextCourierLocation = courierLocations.get(i + 1);
            distance += calculateDistance(courierLocation.getLatitude(), courierLocation.getLongitude(), nextCourierLocation.getLatitude(), nextCourierLocation.getLongitude());
        }

        return ResponseEntity.ok(distance);
    }

    /**
     * Checking if a courier is within range of a certain store
     * @param store latitute of the point1
     * @param courierLocation longitude of the point1
     * @return whether the courier is within a store's area or not
     */
    public static boolean isCourierWithinStoreRange(StoreEntity store, CourierLocationUpdateRequest courierLocation) {
        return calculateDistance(store.getLatitude(), store.getLongitude(), courierLocation.getLatitude(), courierLocation.getLongitude()) <= STORE_PROXIMITY_RADIUS_METERS;
    }

    /**
     * Maps a CourierLocationUpdateRequest to CourierLocationEntity if the request is valid.
     * @throws IllegalArgumentException if request is invalid.
     * @param courierLocationUpdateRequest CourierLocationUpdateRequest coming from controller
     * @return CourierLocationEntity created from incoming CourierLocationUpdateRequest.
     */
    private CourierLocationEntity mapLocationUpdateRequestToLocationEntity(CourierLocationUpdateRequest courierLocationUpdateRequest) {
        if (!CourierLocationUpdateRequest.isValid(courierLocationUpdateRequest)) {
            throw new IllegalArgumentException("Invalid request. Parameters cannot be null or empty.");
        }
        return CourierLocationEntity.builder()
                .courierId(courierLocationUpdateRequest.getCourierId())
                .latitude(courierLocationUpdateRequest.getLatitude())
                .longitude(courierLocationUpdateRequest.getLongitude())
                .timestamp(courierLocationUpdateRequest.getTimestamp())
                .build();
    }



}
