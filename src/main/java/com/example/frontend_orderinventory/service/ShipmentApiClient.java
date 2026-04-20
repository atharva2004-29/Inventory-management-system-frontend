package com.example.frontend_orderinventory.service;

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
public class ShipmentApiClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public ShipmentApiClient(HttpClient httpClient, ObjectMapper objectMapper, @Value("${backend.api.url}") String baseUrl) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl + "/shipments";
    }

    public List<ShipmentDTO> findAll() throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new RuntimeException("Backend returned error " + response.statusCode() + ": " + response.body());
        }

        return objectMapper.readValue(response.body(), new TypeReference<List<ShipmentDTO>>() {});
    }

    public ShipmentDTO findById(Integer id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/" + id)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), ShipmentDTO.class);
    }

    public List<ShipmentDTO> findByStatus(String status) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/status/" + status)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), new TypeReference<List<ShipmentDTO>>() {});
    }

    public List<ShipmentDTO> findByCustomerId(Integer customerId) throws Exception {
        // The backend endpoint is /api/customers/{id}/shipments
        String url = baseUrl.replace("/shipments", "") + "/customers/" + customerId + "/shipments";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), new TypeReference<List<ShipmentDTO>>() {});
    }

    public List<ShipmentDTO> findByStoreId(Integer storeId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/store/" + storeId)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), new TypeReference<List<ShipmentDTO>>() {});
    }

    public ShipmentDTO save(ShipmentDTO dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .header("Content-Type", "application/json");

        if (dto.getShipmentId() == null) {
            builder.uri(URI.create(baseUrl)).POST(HttpRequest.BodyPublishers.ofString(json));
        } else {
            builder.uri(URI.create(baseUrl + "/" + dto.getShipmentId())).PUT(HttpRequest.BodyPublishers.ofString(json));
        }

        HttpRequest request = builder.build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), ShipmentDTO.class);
    }

    public void deleteById(Integer id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/" + id)).DELETE().build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
