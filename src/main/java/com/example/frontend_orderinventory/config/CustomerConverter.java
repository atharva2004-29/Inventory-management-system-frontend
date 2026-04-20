package com.example.frontend_orderinventory.config;

import com.example.frontend_orderinventory.dto.CustomerDTO;
import com.example.frontend_orderinventory.service.CustomerApiClient;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CustomerConverter implements Converter<String, CustomerDTO> {

    private final CustomerApiClient customerApiClient;

    public CustomerConverter(CustomerApiClient customerApiClient) {
        this.customerApiClient = customerApiClient;
    }

    @Override
    public CustomerDTO convert(String id) {
        if (id == null || id.isBlank()) return null;
        try {
            return customerApiClient.findById(Integer.parseInt(id));
        } catch (Exception e) {
            return null;
        }
    }
}
