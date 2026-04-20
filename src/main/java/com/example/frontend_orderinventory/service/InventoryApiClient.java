package com.example.frontend_orderinventory.service;

import com.example.frontend_orderinventory.dto.InventoryDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class InventoryApiClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public InventoryApiClient(HttpClient httpClient, ObjectMapper objectMapper, @Value("${backend.api.url}") String baseUrl) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl + "/inventory";
    }

    public List<InventoryDTO> findAll() throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new RuntimeException("Backend returned error " + response.statusCode() + ": " + response.body());
        }

        return objectMapper.readValue(response.body(), new TypeReference<List<InventoryDTO>>() {});
    }

    public InventoryDTO findById(Integer id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/" + id)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), InventoryDTO.class);
    }

    public List<InventoryDTO> findByProductId(Integer productId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/product/" + productId)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), new TypeReference<List<InventoryDTO>>() {});
    }

    public List<InventoryDTO> findByStoreId(Integer storeId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/store/" + storeId)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), new TypeReference<List<InventoryDTO>>() {});
    }

    public InventoryDTO save(InventoryDTO dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .header("Content-Type", "application/json");

        if (dto.getInventoryId() == null) {
            builder.uri(URI.create(baseUrl)).POST(HttpRequest.BodyPublishers.ofString(json));
        } else {
            builder.uri(URI.create(baseUrl + "/" + dto.getInventoryId())).PUT(HttpRequest.BodyPublishers.ofString(json));
        }

        HttpRequest request = builder.build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), InventoryDTO.class);
    }

    public void deleteById(Integer id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/" + id)).DELETE().build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
