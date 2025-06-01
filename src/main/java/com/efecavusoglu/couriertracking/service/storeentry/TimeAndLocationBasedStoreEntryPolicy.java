package com.efecavusoglu.couriertracking.service.storeentry;

import com.efecavusoglu.couriertracking.model.entity.CourierLocationEntity;
import com.efecavusoglu.couriertracking.model.entity.CourierStoreEntryEntity;
import com.efecavusoglu.couriertracking.model.entity.StoreEntity;
import com.efecavusoglu.couriertracking.repository.CourierStoreEntryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static com.efecavusoglu.couriertracking.util.DistanceUtil.calculateDistance;

/**
 * Store Entry Policy based on time and location criteria.
 * This policy checks if a courier has entered a store before, and if so, it checks if the re-entry cooldown period has passed.
 */
@Component
public class TimeAndLocationBasedStoreEntryPolicy implements StoreEntryPolicy {

    @Value("${couriertracking.store_proximity_radius.meters:100}")
    private double STORE_PROXIMITY_RADIUS_METERS;

    @Value("${couriertracking.reentry_cooldown.minutes:1}")
    private Long REENTRY_COOLDOWN_MINUTES;

    @Override
    public boolean canTriggerStoreEntry(StoreEntity entity, CourierLocationEntity courierLocationEntity, CourierStoreEntryRepository courierStoreEntryRepository) {
        return isCourierWithinStoreRange(entity, courierLocationEntity) && !isCourierEnteredStoreBefore(entity, courierLocationEntity, courierStoreEntryRepository);
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

    /***
     * Checking if a courier has entered a store before.
     * @param store target store
     * @param courierLocation location of the courier
     * @return true if the courier has entered the store before within re-entry consideration, false otherwise.
     */
    private boolean isCourierEnteredStoreBefore(StoreEntity store, CourierLocationEntity courierLocation, CourierStoreEntryRepository courierStoreEntryRepository) {
        List<CourierStoreEntryEntity> courierStoreEntries = courierStoreEntryRepository.findByCourierIdAndStoreIdOrderByTimestampDesc(courierLocation.getCourierId(), store.getId());
        Optional<CourierStoreEntryEntity> priorEntryToStore = courierStoreEntries.stream()
                .filter(entry -> ChronoUnit.MINUTES.between(entry.getTimestamp(), courierLocation.getTimestamp()) <= REENTRY_COOLDOWN_MINUTES)
                .findFirst();
        return priorEntryToStore.isPresent();
    }
}
