package com.example.frontend_orderinventory.config;

import com.example.frontend_orderinventory.dto.ProductDTO;
import com.example.frontend_orderinventory.service.ProductApiClient;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ProductConverter implements Converter<String, ProductDTO> {

    private final ProductApiClient productApiClient;

    public ProductConverter(ProductApiClient productApiClient) {
        this.productApiClient = productApiClient;
    }

    @Override
    public ProductDTO convert(String id) {
        if (id == null || id.isBlank()) return null;
        try {
            return productApiClient.findById(Integer.parseInt(id));
        } catch (Exception e) {
            return null;
        }
    }
}
