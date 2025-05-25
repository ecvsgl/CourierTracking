package com.efecavusoglu.couriertracking.repository;

import com.efecavusoglu.couriertracking.model.entity.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, Long> {

    Optional<StoreEntity> findByStoreName(String name);
}
