package com.efecavusoglu.couriertracking.service;

import com.efecavusoglu.couriertracking.exception.InsufficientDataException;
import com.efecavusoglu.couriertracking.model.dto.CourierLocationUpdateRequest;
import com.efecavusoglu.couriertracking.model.entity.CourierLocationEntity;
import com.efecavusoglu.couriertracking.model.entity.CourierStoreEntryEntity;
import com.efecavusoglu.couriertracking.model.entity.StoreEntity;
import com.efecavusoglu.couriertracking.repository.CourierLocationRepository;
import com.efecavusoglu.couriertracking.repository.CourierStoreEntryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

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
        CourierLocationEntity courierLocationEntity = courierLocationRepository.save(mapLocationUpdateRequestToLocationEntity(courierLocationUpdateRequest));

        // persist to DB if locationUpdate triggered a storeEntry
        evaluateIfStoreEntryTriggered(courierLocationEntity).ifPresent(courierStoreEntryRepository::save);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> processBatchLocationUpdate(List<CourierLocationUpdateRequest> courierLocationList) {
        if (courierLocationList == null || courierLocationList.isEmpty()) {
            throw new IllegalArgumentException("Please provide at least one location update request.");
        }

        // batch persistence for ACID compliance and performance
        List<CourierLocationEntity> courierLocationEntityList = courierLocationRepository.saveAll(courierLocationList.stream()
                .map(this::mapLocationUpdateRequestToLocationEntity)
                .toList());

        courierStoreEntryRepository.saveAll(courierLocationEntityList.stream()
                .map(this::evaluateIfStoreEntryTriggered)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList());

        return ResponseEntity.ok().build();
    }


    private Optional<CourierStoreEntryEntity> evaluateIfStoreEntryTriggered(CourierLocationEntity courierLocationEntity) {
        return storeService.getStores()
                .stream()
                .filter(store ->isCourierWithinStoreRange(store, courierLocationEntity) && !isCourierEnteredStoreBefore(store, courierLocationEntity))
                .findAny()// because the courier can be within range of one store for 100 meters
                .map(store -> mapLocationEntityToStoreEntryEntity(store, courierLocationEntity));
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
        for (int i = 1; i < courierLocations.size(); i++) {
            CourierLocationEntity courierLocation = courierLocations.get(i-1);
            CourierLocationEntity nextCourierLocation = courierLocations.get(i);
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
    private boolean isCourierWithinStoreRange(StoreEntity store, CourierLocationEntity courierLocation) {
        return calculateDistance(store.getLatitude(), store.getLongitude(), courierLocation.getLatitude(), courierLocation.getLongitude()) <= STORE_PROXIMITY_RADIUS_METERS;
    }

    private boolean isCourierEnteredStoreBefore(StoreEntity store, CourierLocationEntity courierLocation) {
        List<CourierStoreEntryEntity> courierStoreEntries = courierStoreEntryRepository.findByCourierIdAndStoreIdOrderByTimestampDesc(courierLocation.getCourierId(), store.getId());
        Optional<CourierStoreEntryEntity> priorEntryToStore = courierStoreEntries.stream()
                .filter(entry -> ChronoUnit.MINUTES.between(entry.getTimestamp(), courierLocation.getTimestamp()) <= REENTRY_COOLDOWN_MINUTES)
                .findFirst();
        return priorEntryToStore.isPresent();
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

    private CourierStoreEntryEntity mapLocationEntityToStoreEntryEntity(StoreEntity store, CourierLocationEntity courierLocationEntity) {
        return CourierStoreEntryEntity.builder()
                .courierId(courierLocationEntity.getCourierId())
                .store(store)
                .timestamp(courierLocationEntity.getTimestamp())
                .build();
    }
}
