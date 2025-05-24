package com.efecavusoglu.couriertracking.service;

import com.efecavusoglu.couriertracking.model.dto.CourierLocationUpdateRequest;
import com.efecavusoglu.couriertracking.model.entity.StoreEntity;
import org.springframework.stereotype.Service;

import static com.efecavusoglu.couriertracking.util.DistanceUtil.calculateDistance;

@Service
public class CourierService {

    private static final double STORE_AREA_RADIUS = 100;

    public void processLocationUpdate(CourierLocationUpdateRequest courierLocation) {
    }












    /**
     * Calculate the total distance traveled by a courier from the start of its first location to the last location.
     * @param courierId courier ID to be checked.
     * @return total distance traveled by a courier, returns null if courierId is not found.
     */
    public Double getTotalTravelDistance(String courierId) {



        return null;
    }

    /**
     * Checking if a courier is within range of a certain store
     * @param store latitute of the point1
     * @param courierLocation longitude of the point1
     * @return whether the courier is within a store's area or not
     */
    public static boolean isCourierWithinStoreRange(StoreEntity store, CourierLocationUpdateRequest courierLocation) {
        return calculateDistance(store.getLatitude(), store.getLongitude(), courierLocation.getLatitude(), courierLocation.getLongitude()) <= STORE_AREA_RADIUS;
    }


}
