package com.efecavusoglu.couriertracking.service;

import com.efecavusoglu.couriertracking.model.dto.StoreDTO;
import com.efecavusoglu.couriertracking.model.entity.StoreEntity;
import com.efecavusoglu.couriertracking.repository.StoreRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
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
    // method to get all stores from stores.json and persist them in database
    private void loadAndPersistStores() {
        try (InputStream inputStream = getClass().getResourceAsStream("/stores.json")) {
            if (inputStream == null) {
                log.error("Resource cannot be found for get");
                return;
            }
            List<StoreDTO> storesFromJsonFile = objectMapper.readValue(inputStream, new TypeReference<>() {});

            storesFromJsonFile.stream().map()


            for (StoreDTO storeInput : storesFromJsonFile) {
                Optional<StoreEntity> storeEntity = storeRepository.findByStoreName(storeInput.getStoreName());
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
        } catch (IOException e) {
            log.error("Failed to load stores from stores.json: {}", e.getMessage(), e);
        }
    }

    public List<StoreEntity> getStores() {
        //ensure stores are loaded
        if (stores == null || stores.isEmpty()) {
            loadStores();
        }
        return stores != null ? Collections.unmodifiableList(stores) : Collections.emptyList();
    }
}
