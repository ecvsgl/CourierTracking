package com.efecavusoglu.couriertracking.service;

import com.efecavusoglu.couriertracking.model.dto.StoreDTO;
import com.efecavusoglu.couriertracking.model.entity.StoreEntity;
import com.efecavusoglu.couriertracking.repository.StoreRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class StoreService {

    private final ObjectMapper objectMapper;
    private final StoreRepository storeRepository;

    public StoreService(ObjectMapper objectMapper, StoreRepository storeRepository) {
        this.objectMapper = objectMapper;
        this.storeRepository = storeRepository;
    }

    @PostConstruct
    //in the future, if this is used even after postConstruct, we'll need to ensure that the cache is wiped off since new data might be inserted
    @CacheEvict(value = "stores", allEntries = true)
    // method to get all stores from stores.json and persist them in the database
    public void loadAndPersistStores() {
        try (InputStream inputStream = getClass().getResourceAsStream("/stores.json")) {
            if (inputStream == null) {
                log.error("Resource cannot be found for get");
                return;
            }
            List<StoreDTO> storesFromJsonFile = objectMapper.readValue(inputStream, new TypeReference<>() {});

            for (StoreDTO storeInput : storesFromJsonFile) {
                Optional<StoreEntity> storeEntity = storeRepository.findByStoreName(storeInput.getStoreName());

                // we will use H2DB for ease of demonstration, an in memory db, it will be empty each time
                // but, if db vendor is changed later on && after app restart the new db did not drop prev values,
                // we can update the preexisting ones as using stores.json as a "single source of truth".
                storeEntity.ifPresentOrElse(entity -> {
                    entity.setLatitude(storeInput.getLatitude());
                    entity.setLongitude(storeInput.getLongitude());
                    storeRepository.save(entity);
                    log.info("Store {} updated successfully", entity.getStoreName());
                }, () -> {
                    StoreEntity store = StoreEntity.builder()
                            .storeName(storeInput.getStoreName())
                            .latitude(storeInput.getLatitude())
                            .longitude(storeInput.getLongitude())
                            .build();
                    storeRepository.save(store);
                    log.info("Store {} persisted successfully", store.getStoreName());
                });
            }
            log.info("All stores persisted successfully.");
        } catch (IOException e) {
            log.error("Failed to load stores from stores.json: {}", e.getMessage(), e);
        }
    }

    // we use caching to avoid excessive calls to DB, since the stores are read once and will not change again.
    @Cacheable("stores")
    public List<StoreEntity> getStores() {
        return storeRepository.findAll();
    }
}
