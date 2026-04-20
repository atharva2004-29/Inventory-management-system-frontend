package com.example.frontend_orderinventory.service;

import com.example.frontend_orderinventory.dto.StoreDTO;
import com.example.frontend_orderinventory.dto.InventoryDTO;
import com.example.frontend_orderinventory.dto.OrderDTO;
import com.example.frontend_orderinventory.dto.ShipmentDTO;
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
public class StoreApiClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public StoreApiClient(HttpClient httpClient, ObjectMapper objectMapper, @Value("${backend.api.url}") String baseUrl) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl + "/stores";
    }

    public List<StoreDTO> findAll() throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new RuntimeException("Backend returned error " + response.statusCode() + ": " + response.body());
        }

        return objectMapper.readValue(response.body(), new TypeReference<List<StoreDTO>>() {});
    }

    public StoreDTO findById(Integer id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/" + id)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), StoreDTO.class);
    }

    public List<StoreDTO> searchByName(String name) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/search?name=" + name)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), new TypeReference<List<StoreDTO>>() {});
    }

    public List<InventoryDTO> getInventory(Integer id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/" + id + "/inventory")).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), new TypeReference<List<InventoryDTO>>() {});
    }

    public List<OrderDTO> getOrders(Integer id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/" + id + "/orders")).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), new TypeReference<List<OrderDTO>>() {});
    }

    public List<ShipmentDTO> getShipments(Integer id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/" + id + "/shipments")).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), new TypeReference<List<ShipmentDTO>>() {});
    }

    public StoreDTO save(StoreDTO dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .header("Content-Type", "application/json");

        if (dto.getStoreId() == null) {
            builder.uri(URI.create(baseUrl)).POST(HttpRequest.BodyPublishers.ofString(json));
        } else {
            builder.uri(URI.create(baseUrl + "/" + dto.getStoreId())).PUT(HttpRequest.BodyPublishers.ofString(json));
        }

        HttpRequest request = builder.build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), StoreDTO.class);
    }

    public void deleteById(Integer id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/" + id)).DELETE().build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
