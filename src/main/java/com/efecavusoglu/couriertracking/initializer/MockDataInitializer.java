package com.efecavusoglu.couriertracking.initializer;

import com.efecavusoglu.couriertracking.model.entity.CourierLocationEntity;
import com.efecavusoglu.couriertracking.model.entity.CourierStoreEntryEntity;
import com.efecavusoglu.couriertracking.model.entity.StoreEntity;
import com.efecavusoglu.couriertracking.repository.CourierLocationRepository;
import com.efecavusoglu.couriertracking.repository.CourierStoreEntryRepository;
import com.efecavusoglu.couriertracking.repository.StoreRepository;
import com.efecavusoglu.couriertracking.util.DistanceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class MockDataInitializer implements CommandLineRunner {

    @Value("${couriertracking.mock.initialize:false}")
    private boolean initializeMockData;

    @Value("${couriertracking.store_proximity_radius.meters:100}")
    private double STORE_PROXIMITY_RADIUS_METERS;

    private final CourierLocationRepository courierLocationRepository;
    private final CourierStoreEntryRepository courierStoreEntryRepository;
    private final StoreRepository storeRepository;
    private final Random random = new Random();

    private static final int NUM_COURIERS = 5;
    private static final int LOCATIONS_PER_COURIER = 17;

    // Coordinates to be scattered around Istanbul
    private static final double BASE_LAT = 40.993142;
    private static final double BASE_LNG = 29.084008;
    private static final double LAT_LNG_SPREAD = 0.05; // randomness spread

    @Override
    public void run(String... args) throws Exception {
        if (initializeMockData) {
            log.info("Initializing mock data...");
            // clear the repositories if they contain any remnant data
            courierLocationRepository.deleteAll();
            courierStoreEntryRepository.deleteAll();
            generateMockData();
            log.info("Mock data initialization completed.");
        } else {
            log.info("Skipping mock data initialization.");
        }
    }

    private void generateMockData() {
        List<StoreEntity> stores = storeRepository.findAll();
        // This is not possible however, just in case of a failure in the JSON to DB persistence...
        if (stores.isEmpty()) {
            return; // No stores exist, so no mock data can be generated. Skip this step.
        }

        LocalDateTime twoHoursAgoTime = LocalDateTime.now().minusHours(2); // Start 2 hours ago

        for (int i = 1; i <= NUM_COURIERS; i++) {
            String courierId = "MOCK_COURIER_ID_" + i;
            log.info("Generating data for courier: {}", courierId);

            for (int j = 0; j < LOCATIONS_PER_COURIER; j++) {
                twoHoursAgoTime = twoHoursAgoTime.plusMinutes(random.nextInt(5) + 1); // Advance time by 1-5 minutes for randomness
                double latitude, longitude;

                // Every few locations, try to make one near a store if stores exist, to populate storeEntry table
                if (j % 4 == 0 && j > 0) { // e.g., 4th, 8th, 12th location
                    StoreEntity targetStore = stores.get(random.nextInt(stores.size()));
                    // Generate coordinates very close to the target store (within ~50m)
                    latitude = targetStore.getLatitude() + (random.nextDouble() - 0.5) * 0.0009; // Approx +/- 50m
                    longitude = targetStore.getLongitude() + (random.nextDouble() - 0.5) * 0.0009;
                    double distance = DistanceUtil.calculateDistance(targetStore.getLatitude(), targetStore.getLongitude(), latitude, longitude);
                    log.info("  Location {} for courier {} is near store: {} by distance: {}", j, courierId, targetStore.getStoreName(), distance);
                } else {
                    // Generate a more random location
                    latitude = BASE_LAT + (random.nextDouble() - 0.5) * LAT_LNG_SPREAD;
                    longitude = BASE_LNG + (random.nextDouble() - 0.5) * LAT_LNG_SPREAD;
                }

                CourierLocationEntity location = CourierLocationEntity.builder()
                        .courierId(courierId)
                        .latitude(latitude)
                        .longitude(longitude)
                        .timestamp(twoHoursAgoTime)
                        .build();
                courierLocationRepository.save(location);

                // Check if this location triggers a store entry, then persist db
                for (StoreEntity store : stores) {
                    double distance = DistanceUtil.calculateDistance(store.getLatitude(), store.getLongitude(), latitude, longitude);
                    if (distance <= STORE_PROXIMITY_RADIUS_METERS) {
                        // For simplicity, we're not checking the REENTRY_COOLDOWN_MINUTES here
                        CourierStoreEntryEntity entry = CourierStoreEntryEntity.builder()
                                .courierId(courierId)
                                .store(store)
                                .timestamp(twoHoursAgoTime)
                                .build();
                        courierStoreEntryRepository.save(entry);
                        log.info("    Courier {} entered store {} at {}. Distance: {}m", courierId, store.getStoreName(), twoHoursAgoTime, distance);
                        break; // Courier enters only one store per location -- multiple stores cannot exist in 100m proximity.
                    }
                }
            }
        }

    }
}
