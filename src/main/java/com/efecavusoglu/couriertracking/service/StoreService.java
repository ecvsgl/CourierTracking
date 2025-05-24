package com.efecavusoglu.couriertracking.service;

import com.efecavusoglu.couriertracking.model.entity.StoreEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class StoreService {

    private List<StoreEntity> stores;
    private final ObjectMapper objectMapper;

    public StoreService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        loadStores();
    }

    //will load stores from stores.json under resources
    private void loadStores() {
        try (InputStream inputStream = getClass().getResourceAsStream("/stores.json")) {
            if (inputStream == null) {
                log.error("Error: stores.json not found in classpath.");
                this.stores = Collections.emptyList();
                return;
            }
            this.stores = objectMapper.readValue(inputStream, new TypeReference<List<StoreEntity>>() {});
            log.info("Successfully loaded {} stores from stores.json.", (this.stores != null ? this.stores.size() : 0));
        } catch (IOException e) {
            log.error("Failed to load stores from stores.json: {}", e.getMessage(), e);
            this.stores = Collections.emptyList();
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
