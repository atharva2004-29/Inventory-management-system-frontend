package com.example.frontend_orderinventory.service;

import com.example.frontend_orderinventory.dto.CustomerDTO;
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
public class CustomerApiClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public CustomerApiClient(HttpClient httpClient, ObjectMapper objectMapper, @Value("${backend.api.url}") String baseUrl) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl + "/customers";
    }

    public List<CustomerDTO> findAll() throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() >= 400) {
            throw new RuntimeException("Backend returned error " + response.statusCode() + ": " + response.body());
        }
        
        return objectMapper.readValue(response.body(), new TypeReference<List<CustomerDTO>>() {});
    }

    public CustomerDTO findById(Integer id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/" + id)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), CustomerDTO.class);
    }

    public List<CustomerDTO> searchByName(String name) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/search?name=" + name)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), new TypeReference<List<CustomerDTO>>() {});
    }

    public CustomerDTO save(CustomerDTO customer) throws Exception {
        String json = objectMapper.writeValueAsString(customer);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .header("Content-Type", "application/json");

        if (customer.getCustomerId() == null) {
            builder.uri(URI.create(baseUrl)).POST(HttpRequest.BodyPublishers.ofString(json));
        } else {
            builder.uri(URI.create(baseUrl + "/" + customer.getCustomerId())).PUT(HttpRequest.BodyPublishers.ofString(json));
        }

        HttpRequest request = builder.build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), CustomerDTO.class);
    }

    public void deleteById(Integer id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/" + id)).DELETE().build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public CustomerDTO findByEmail(String email) throws Exception {
        return findAll().stream()
                .filter(c -> email.equals(c.getEmailAddress()))
                .findFirst()
                .orElse(null);
    }
}
