package com.example.frontend_orderinventory.config;

import com.example.frontend_orderinventory.dto.StoreDTO;
import com.example.frontend_orderinventory.service.StoreApiClient;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StoreConverter implements Converter<String, StoreDTO> {

    private final StoreApiClient storeApiClient;

    public StoreConverter(StoreApiClient storeApiClient) {
        this.storeApiClient = storeApiClient;
    }

    @Override
    public StoreDTO convert(String id) {
        if (id == null || id.isBlank()) return null;
        try {
            return storeApiClient.findById(Integer.parseInt(id));
        } catch (Exception e) {
            return null;
        }
    }
}
