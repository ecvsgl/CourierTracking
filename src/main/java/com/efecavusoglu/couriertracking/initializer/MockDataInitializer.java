package com.efecavusoglu.couriertracking.initializer;

import com.efecavusoglu.couriertracking.model.entity.StoreEntity;
import com.efecavusoglu.couriertracking.repository.CourierLocationRepository;
import com.efecavusoglu.couriertracking.repository.CourierStoreEntryRepository;
import com.efecavusoglu.couriertracking.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class MockDataInitializer implements CommandLineRunner {

    @Value("${couriertracking.mock.initialize:false}")
    private boolean initializeMockData;

    @Value("${couriertracking.store_proximity_radius.meters:100}")
    private static double STORE_PROXIMITY_RADIUS_METERS;

    @Value("${couriertracking.reentry_cooldown.minutes:1}")
    private static Long REENTRY_COOLDOWN_MINUTES;

    private final CourierLocationRepository courierLocationRepository;
    private final CourierStoreEntryRepository courierStoreEntryRepository;
    private final StoreRepository storeRepository;
    private final Random random = new Random();

    private static final int NUM_COURIERS = 5;
    private static final int LOCATIONS_PER_COURIER = 10;

    // Coordinates to be scatter around Istanbul
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
        } else {
            log.info("Skipping mock data initialization.");
        }
    }

    private void generateMockData() {

    }
}
