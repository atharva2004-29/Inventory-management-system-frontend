package com.example.frontend_orderinventory.service;

import com.example.frontend_orderinventory.dto.ProductDTO;
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
public class ProductApiClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public ProductApiClient(HttpClient httpClient, ObjectMapper objectMapper, @Value("${backend.api.url}") String baseUrl) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl + "/products";
    }

    public List<ProductDTO> findAll() throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new RuntimeException("Backend returned error " + response.statusCode() + ": " + response.body());
        }

        return objectMapper.readValue(response.body(), new TypeReference<List<ProductDTO>>() {});
    }

    public ProductDTO findById(Integer id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/" + id)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), ProductDTO.class);
    }

    public List<ProductDTO> searchByName(String name) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/search?name=" + name)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), new TypeReference<List<ProductDTO>>() {});
    }

    public ProductDTO save(ProductDTO dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .header("Content-Type", "application/json");

        if (dto.getProductId() == null) {
            builder.uri(URI.create(baseUrl)).POST(HttpRequest.BodyPublishers.ofString(json));
        } else {
            builder.uri(URI.create(baseUrl + "/" + dto.getProductId())).PUT(HttpRequest.BodyPublishers.ofString(json));
        }

        HttpRequest request = builder.build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), ProductDTO.class);
    }

    public void deleteById(Integer id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/" + id)).DELETE().build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
