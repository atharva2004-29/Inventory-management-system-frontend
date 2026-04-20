package com.example.frontend_orderinventory.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class AppConfig {

    @Bean
    public HttpClient httpClient() {
        // Build a standard JDK HttpClient with a 10-second timeout
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // This module is necessary if your Backend uses Java 8 dates (LocalDate/LocalDateTime)
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}