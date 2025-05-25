package com.efecavusoglu.couriertracking.util;

import com.efecavusoglu.couriertracking.model.dto.CourierLocationUpdateRequest;
import com.efecavusoglu.couriertracking.model.dto.CourierLocationUpdateResponse;
import com.efecavusoglu.couriertracking.model.entity.CourierLocationEntity;
import com.efecavusoglu.couriertracking.model.entity.CourierStoreEntryEntity;
import com.efecavusoglu.couriertracking.model.entity.StoreEntity;

/**
 * Class that contains utility methods for mapping objects.
 */
public class MapperUtil {

    private MapperUtil(){
        // constructor is private -- preventing instantiation of the utility class
    }

    /**
     * Maps a CourierLocationUpdateRequest to CourierLocationEntity if the request is valid.
     * @throws IllegalArgumentException if the request is invalid.
     * @param courierLocationUpdateRequest CourierLocationUpdateRequest coming from controller
     * @return CourierLocationEntity created from incoming CourierLocationUpdateRequest.
     */
    public static CourierLocationEntity mapLocationUpdateRequestToLocationEntity(CourierLocationUpdateRequest courierLocationUpdateRequest) {
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

    /**
     * Maps a CourierLocationEntity to a CourierStoreEntryEntity.
     */
    public static CourierStoreEntryEntity mapLocationEntityToStoreEntryEntity(StoreEntity store, CourierLocationEntity courierLocationEntity) {
        return CourierStoreEntryEntity.builder()
                .courierId(courierLocationEntity.getCourierId())
                .store(store)
                .timestamp(courierLocationEntity.getTimestamp())
                .build();
    }
    /**
     * Maps a CourierLocationEntity to a CourierLocationUpdateResponse.
     */
    public static CourierLocationUpdateResponse mapLocationEntityToLocationResponse(CourierLocationEntity courierLocationEntity) {
        return CourierLocationUpdateResponse.builder()
                .courierId(courierLocationEntity.getCourierId())
                .longitude(courierLocationEntity.getLongitude())
                .latitude(courierLocationEntity.getLatitude())
                .timestamp(courierLocationEntity.getTimestamp())
                .isTriggeredStoreEntry(false)
                .build();
    }


}
