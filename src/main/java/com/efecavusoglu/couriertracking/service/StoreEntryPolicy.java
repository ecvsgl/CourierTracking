package com.efecavusoglu.couriertracking.service;

import com.efecavusoglu.couriertracking.model.entity.CourierLocationEntity;
import com.efecavusoglu.couriertracking.model.entity.StoreEntity;
import com.efecavusoglu.couriertracking.repository.CourierStoreEntryRepository;

/*
 * Interface to define a policy for triggering store entry -- Strategy Pattern.
 * Enables extensibility for future.
 */
public interface StoreEntryPolicy {
    boolean canTriggerStoreEntry(StoreEntity entity, CourierLocationEntity courierLocationEntity, CourierStoreEntryRepository courierStoreEntryRepository);
}
