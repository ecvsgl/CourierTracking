package com.efecavusoglu.couriertracking.service;

import com.efecavusoglu.couriertracking.exception.InsufficientDataException;
import com.efecavusoglu.couriertracking.model.dto.CourierLocationUpdateRequest;
import com.efecavusoglu.couriertracking.model.dto.CourierLocationUpdateResponse;
import com.efecavusoglu.couriertracking.model.entity.CourierLocationEntity;
import com.efecavusoglu.couriertracking.model.entity.CourierStoreEntryEntity;
import com.efecavusoglu.couriertracking.repository.CourierLocationRepository;
import com.efecavusoglu.couriertracking.repository.CourierStoreEntryRepository;
import com.efecavusoglu.couriertracking.util.MapperUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.efecavusoglu.couriertracking.util.DistanceUtil.calculateDistance;

@Service
public class CourierService {

    private final StoreService storeService;
    private final CourierLocationRepository courierLocationRepository;
    private final CourierStoreEntryRepository courierStoreEntryRepository;
    private final StoreEntryPolicy storeEntryPolicy;

    public CourierService(StoreService storeService, CourierLocationRepository courierLocationRepository, CourierStoreEntryRepository courierStoreEntryRepository) {
        this.storeService = storeService;
        this.courierLocationRepository = courierLocationRepository;
        this.courierStoreEntryRepository = courierStoreEntryRepository;
        this.storeEntryPolicy = TimeAndLocationBasedStoreEntryPolicy.getInstance();
    }

    /**
     * Process a single location update request.
     * Converts request to a locationEntity and persists it to DB.
     * Checks if the locationUpdate is eligible to trigger a storeEntry and persists it to DB if so.
     * @param courierLocationUpdateRequest
     * @return ResponseEntity<CourierLocationUpdateResponse> with the locationUpdate response.
     */
    @Transactional
    public ResponseEntity<CourierLocationUpdateResponse> processSingleLocationUpdate(CourierLocationUpdateRequest courierLocationUpdateRequest) {
        //persist location to DB
        CourierLocationEntity courierLocationEntity = courierLocationRepository.save(MapperUtil.mapLocationUpdateRequestToLocationEntity(courierLocationUpdateRequest));

        //create response from entity
        CourierLocationUpdateResponse courierLocationUpdateResponse = MapperUtil.mapLocationEntityToLocationResponse(courierLocationEntity);

        // persist to DB if locationUpdate triggered a storeEntry, and tag response storeEntryTrigger to true
        evaluateIfStoreEntryTriggered(courierLocationEntity).ifPresent(storeEntry -> {
            courierStoreEntryRepository.save(storeEntry);
            courierLocationUpdateResponse.setTriggeredStoreEntry(true);
        });

        return ResponseEntity.ok(courierLocationUpdateResponse);
    }

    /**
     * Process a batch of location update requests.
     * @param courierLocationList locationUpdateRequests to be processed.
     * @return ResponseEntity<CourierLocationUpdateResponse> ResponseEntity with a list of locationUpdate response.
     */
    @Transactional
    public ResponseEntity<List<CourierLocationUpdateResponse>> processBatchLocationUpdate(List<CourierLocationUpdateRequest> courierLocationList) {
        if (courierLocationList == null || courierLocationList.isEmpty()) {
            throw new IllegalArgumentException("Please provide at least one location update request.");
        }

        // batch persistence of locations for ACID compliance and performance
        List<CourierLocationEntity> courierLocationEntityList = courierLocationRepository.saveAll(courierLocationList.stream()
                .map(MapperUtil::mapLocationUpdateRequestToLocationEntity)
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
            CourierLocationUpdateResponse courierLocationUpdateResponse = MapperUtil.mapLocationEntityToLocationResponse(courierLocationEntity);

            evaluateIfStoreEntryTriggered(courierLocationEntity).ifPresent(storeEntry -> {
                storeEntryList.add(storeEntry);
                courierLocationUpdateResponse.setTriggeredStoreEntry(true);
            });

            responseList.add(courierLocationUpdateResponse);
        }

        courierStoreEntryRepository.saveAll(storeEntryList);

        return ResponseEntity.ok(responseList);
    }

    /**
     * Evaluate if a locationUpdate is eligible to trigger a storeEntry.
     * @param courierLocationEntity locationUpdate entity to be evaluated.
     * @return Optional<CourierStoreEntryEntity> if the locationUpdate is eligible to trigger a storeEntry, returns empty Optional otherwise.
     */
    private Optional<CourierStoreEntryEntity> evaluateIfStoreEntryTriggered(CourierLocationEntity courierLocationEntity) {
        return storeService.getStores()
                .stream()
                .filter(store -> storeEntryPolicy.canTriggerStoreEntry(store, courierLocationEntity, courierStoreEntryRepository))
                .findAny() // because the courier can be within range of one store for 100 meters per locationUpdate
                .map(store -> MapperUtil.mapLocationEntityToStoreEntryEntity(store, courierLocationEntity));
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
}
