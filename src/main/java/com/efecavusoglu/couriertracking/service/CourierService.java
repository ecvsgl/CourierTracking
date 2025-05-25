package com.efecavusoglu.couriertracking.service;

import com.efecavusoglu.couriertracking.exception.InsufficientDataException;
import com.efecavusoglu.couriertracking.model.dto.CourierLocationUpdateRequest;
import com.efecavusoglu.couriertracking.model.dto.CourierLocationUpdateResponse;
import com.efecavusoglu.couriertracking.model.dto.CourierStoreEntryResponse;
import com.efecavusoglu.couriertracking.model.entity.CourierLocationEntity;
import com.efecavusoglu.couriertracking.model.entity.CourierStoreEntryEntity;
import com.efecavusoglu.couriertracking.model.entity.StoreEntity;
import com.efecavusoglu.couriertracking.repository.CourierLocationRepository;
import com.efecavusoglu.couriertracking.repository.CourierStoreEntryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.efecavusoglu.couriertracking.util.DistanceUtil.calculateDistance;

@Service
public class CourierService {

    @Value("${couriertracking.store_proximity_radius.meters:100}")
    private static double STORE_PROXIMITY_RADIUS_METERS;

    @Value("${couriertracking.reentry_cooldown.minutes:1}")
    private static Long REENTRY_COOLDOWN_MINUTES;

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
    public ResponseEntity<CourierLocationUpdateResponse> processSingleLocationUpdate(CourierLocationUpdateRequest courierLocationUpdateRequest) {
        //persist location to DB
        CourierLocationEntity courierLocationEntity = courierLocationRepository.save(mapLocationUpdateRequestToLocationEntity(courierLocationUpdateRequest));

        //create response from entity
        CourierLocationUpdateResponse courierLocationUpdateResponse = mapLocationEntityToLocationResponse(courierLocationEntity);

        // persist to DB if locationUpdate triggered a storeEntry, and tag response storeEntryTrigger to true
        evaluateIfStoreEntryTriggered(courierLocationEntity).ifPresent(storeEntry -> {
            courierStoreEntryRepository.save(storeEntry);
            courierLocationUpdateResponse.setTriggeredStoreEntry(true);
        });

        return ResponseEntity.ok(courierLocationUpdateResponse);
    }

    public ResponseEntity<List<CourierLocationUpdateResponse>> processBatchLocationUpdate(List<CourierLocationUpdateRequest> courierLocationList) {
        if (courierLocationList == null || courierLocationList.isEmpty()) {
            throw new IllegalArgumentException("Please provide at least one location update request.");
        }

        // batch persistence of locations for ACID compliance and performance
        List<CourierLocationEntity> courierLocationEntityList = courierLocationRepository.saveAll(courierLocationList.stream()
                .map(this::mapLocationUpdateRequestToLocationEntity)
                .toList());

        // We are ordering by courierIds first. Then by timestamp.
        // Why? Because if the data comes in unordered with respect to timestamp, wrong location activity might be associated with storeEntry
        courierLocationEntityList.sort(Comparator.comparing(CourierLocationEntity::getCourierId).thenComparing(CourierLocationEntity::getTimestamp));

        // iterate over locationEntity list for:
        // 1) create a corresponding response for each entity,
        // 2) if such locationUpdateEntity triggers a storeEntry, create storeEntryEntity and add it to the persistence list, then mark response for successful storeEntry
        List<CourierLocationUpdateResponse> responseList = new LinkedList<>();
        List<CourierStoreEntryEntity> storeEntryList = new LinkedList<>();

        for (int i = 0; i < courierLocationEntityList.size(); i++) {
            CourierLocationEntity courierLocationEntity = courierLocationEntityList.get(i);
            CourierLocationUpdateResponse courierLocationUpdateResponse = mapLocationEntityToLocationResponse(courierLocationEntity);

            evaluateIfStoreEntryTriggered(courierLocationEntity).ifPresent(storeEntry -> {
                storeEntryList.add(storeEntry);
                courierLocationUpdateResponse.setTriggeredStoreEntry(true);
            });

            responseList.add(courierLocationUpdateResponse);
        }

        courierStoreEntryRepository.saveAll(storeEntryList);

        return ResponseEntity.ok(responseList);
    }


    private Optional<CourierStoreEntryEntity> evaluateIfStoreEntryTriggered(CourierLocationEntity courierLocationEntity) {
        return storeService.getStores()
                .stream()
                .filter(store -> isCourierWithinStoreRange(store, courierLocationEntity))
                .filter(store -> !isCourierEnteredStoreBefore(store, courierLocationEntity))
                .findAny() // because the courier can be within range of one store for 100 meters per locationUpdate
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

        if (courierLocations.isEmpty()){
            throw new EntityNotFoundException("No data found for courier with ID: " + courierId);
        }

        if (courierLocations.size() < 2) {
            throw new InsufficientDataException("Not enough data for calculation. Please provide at least 2 locations for the courier to calculate distance from the start to the end. ");
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
     * @throws IllegalArgumentException if the request is invalid.
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

    private CourierLocationUpdateResponse mapLocationEntityToLocationResponse(CourierLocationEntity courierLocationEntity) {
        return CourierLocationUpdateResponse.builder()
                .courierId(courierLocationEntity.getCourierId())
                .longitude(courierLocationEntity.getLongitude())
                .latitude(courierLocationEntity.getLatitude())
                .timestamp(courierLocationEntity.getTimestamp())
                .isTriggeredStoreEntry(false)
                .build();
    }

    public ResponseEntity<List<CourierLocationEntity>> getAllLocations() {
        return ResponseEntity.ok(courierLocationRepository.findAll());
    }

    public ResponseEntity<List<CourierStoreEntryResponse>> getAllStoreEntries() {
        return ResponseEntity.ok(courierStoreEntryRepository.findAll().stream().map(this::mapStoreEntryEntityToResponse).toList());
    }

    private CourierStoreEntryResponse mapStoreEntryEntityToResponse(CourierStoreEntryEntity entity){
        return CourierStoreEntryResponse.builder()
                .storeName(entity.getStore().getStoreName())
                .courierId(entity.getCourierId())
                .timestamp(entity.getTimestamp())
                .build();

    }
}
