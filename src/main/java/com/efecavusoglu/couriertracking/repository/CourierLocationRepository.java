package com.efecavusoglu.couriertracking.repository;

import com.efecavusoglu.couriertracking.model.entity.CourierLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourierLocationRepository extends JpaRepository<CourierLocationEntity, Long> {

    List<CourierLocationEntity> findByCourierIdOrderByTimestampAsc(String courierId);
}
