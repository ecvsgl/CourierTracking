package com.efecavusoglu.couriertracking.repository;

import com.efecavusoglu.couriertracking.model.entity.CourierStoreEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourierStoreEntryRepository extends JpaRepository<CourierStoreEntryEntity, Long> {
}
