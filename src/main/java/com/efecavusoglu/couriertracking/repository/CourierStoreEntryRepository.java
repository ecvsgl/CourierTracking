package com.efecavusoglu.couriertracking.repository;

import com.efecavusoglu.couriertracking.model.entity.CourierStoreEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourierStoreEntryRepository extends JpaRepository<CourierStoreEntryEntity, Long> {

    List<CourierStoreEntryEntity> findByCourierIdOrderByTimestampAsc(String courierId);
}
