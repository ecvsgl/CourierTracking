package com.efecavusoglu.couriertracking.service;

import com.efecavusoglu.couriertracking.model.CourierLocation;
import com.efecavusoglu.couriertracking.model.Store;
import org.springframework.stereotype.Service;

import static com.efecavusoglu.couriertracking.util.DistanceUtil.calculateDistance;

@Service
public class CourierService {

    private static final double STORE_AREA_RADIUS = 100;

















    /**
     * Checking if a courier is within range of a certain store
     * @param store latitute of the point1
     * @param courierLocation longitude of the point1
     * @return whether the courier is within a store's area or not
     */
    public static boolean isCourierWithinStoreRange(Store store, CourierLocation courierLocation) {
        return calculateDistance(store.getLatitude(), store.getLongitude(), courierLocation.getLatitude(), courierLocation.getLongitude()) <= STORE_AREA_RADIUS;
    }

}
