package com.efecavusoglu.couriertracking.service.storeentry;

import com.efecavusoglu.couriertracking.model.entity.CourierLocationEntity;
import com.efecavusoglu.couriertracking.model.entity.StoreEntity;
import com.efecavusoglu.couriertracking.repository.CourierStoreEntryRepository;

/*
 * Interface to define a policy for triggering store.
 * Enables extensibility for future.
 */
public interface StoreEntryPolicy {
    boolean canTriggerStoreEntry(StoreEntity entity, CourierLocationEntity courierLocationEntity, CourierStoreEntryRepository courierStoreEntryRepository);
}
