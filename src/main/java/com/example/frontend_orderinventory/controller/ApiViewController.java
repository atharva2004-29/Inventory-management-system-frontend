package com.example.frontend_orderinventory.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.frontend_orderinventory.dto.*;
import com.example.frontend_orderinventory.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/view/api")
public class ApiViewController {

    private final CustomerApiClient customerApiClient;
    private final ProductApiClient productApiClient;
    private final StoreApiClient storeApiClient;
    private final OrderApiClient orderApiClient;
    private final ShipmentApiClient shipmentApiClient;
    private final InventoryApiClient inventoryApiClient;
    private final ObjectMapper objectMapper;

    public ApiViewController(CustomerApiClient customerApiClient, ProductApiClient productApiClient,
                             StoreApiClient storeApiClient, OrderApiClient orderApiClient,
                             ShipmentApiClient shipmentApiClient, InventoryApiClient inventoryApiClient,
                             ObjectMapper objectMapper) {
        this.customerApiClient = customerApiClient;
        this.productApiClient = productApiClient;
        this.storeApiClient = storeApiClient;
        this.orderApiClient = orderApiClient;
        this.shipmentApiClient = shipmentApiClient;
        this.inventoryApiClient = inventoryApiClient;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/{entity}")
    public String listEntity(@PathVariable String entity, Model model) {
        try {
            Object rawData = switch (entity) {
                case "customers" -> customerApiClient.findAll();
                case "products" -> productApiClient.findAll();
                case "stores" -> storeApiClient.findAll();
                case "orders" -> orderApiClient.findAll();
                case "shipments" -> shipmentApiClient.findAll();
                case "inventory" -> inventoryApiClient.findAll();
                default -> Collections.emptyList();
            };

            model.addAttribute("title", "All " + capitalize(entity));
            model.addAttribute("type", "LIST");
            model.addAttribute("data", convertToMapList(rawData));
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
        }
        return "api-result";
    }

    @GetMapping("/{entity}/{id}")
    public String getEntityById(@PathVariable String entity, @PathVariable Integer id, Model model) {
        try {
            Object rawData = switch (entity) {
                case "customers" -> customerApiClient.findById(id);
                case "products" -> productApiClient.findById(id);
                case "stores" -> storeApiClient.findById(id);
                case "orders" -> orderApiClient.findById(id);
                case "shipments" -> shipmentApiClient.findById(id);
                case "inventory" -> inventoryApiClient.findById(id);
                default -> null;
            };

            model.addAttribute("title", capitalize(entity) + " Details (ID: " + id + ")");
            model.addAttribute("type", "SINGLE");
            model.addAttribute("data", convertToMap(rawData));
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
        }
        return "api-result";
    }

    @GetMapping("/{entity}/search")
    public String search(@PathVariable String entity, @RequestParam(defaultValue = "test") String name, Model model) {
        try {
            Object rawData = switch (entity) {
                case "customers" -> customerApiClient.searchByName(name);
                case "products" -> productApiClient.searchByName(name);
                case "stores" -> storeApiClient.searchByName(name);
                default -> Collections.emptyList();
            };

            model.addAttribute("title", capitalize(entity) + " Search Results for '" + name + "'");
            model.addAttribute("type", "LIST");
            model.addAttribute("data", convertToMapList(rawData));
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
        }
        return "api-result";
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private List<Map<String, Object>> convertToMapList(Object obj) {
        if (obj == null) return Collections.emptyList();
        try {
            List<Map<String, Object>> raw = objectMapper.convertValue(obj, new TypeReference<List<Map<String, Object>>>() {});
            return raw.stream().map(this::flattenForDisplay).toList();
        } catch (Exception e) {
            try {
                Map<String, Object> map = convertToMap(obj);
                return map != null ? List.of(map) : Collections.emptyList();
            } catch (Exception e2) {
                return Collections.emptyList();
            }
        }
    }

    private Map<String, Object> convertToMap(Object obj) {
        if (obj == null) return null;
        try {
            Map<String, Object> raw = objectMapper.convertValue(obj, new TypeReference<Map<String, Object>>() {});
            return flattenForDisplay(raw);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> flattenForDisplay(Map<String, Object> input) {
        Map<String, Object> result = new java.util.LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();

            if (val instanceof Map) {
                // Flatten nested object to a human-readable string
                result.put(key, readableNestedObject(key, (Map<String, Object>) val));
            } else if (val instanceof List) {
                // Check if it's a date-like list e.g. [2023, 1, 3, 0, 0]
                result.put(key, readableList(key, (List<?>) val));
            } else {
                result.put(key, val);
            }
        }
        return result;
    }

    private String readableNestedObject(String key, Map<String, Object> nested) {
        // Try to return only the most meaningful fields
        if (nested.containsKey("fullName")) {
            String name = String.valueOf(nested.get("fullName"));
            Object email = nested.get("emailAddress");
            return email != null ? name + " (" + email + ")" : name;
        }
        if (nested.containsKey("storeName")) {
            String name = String.valueOf(nested.get("storeName"));
            Object web = nested.get("webAddress");
            Object addr = nested.get("physicalAddress");
            if (web != null && !web.toString().isBlank()) return name + " — " + web;
            if (addr != null && !addr.toString().isBlank()) return name + " — " + addr.toString().replace("\n", ", ").trim();
            return name;
        }
        if (nested.containsKey("productName")) {
            String name = String.valueOf(nested.get("productName"));
            Object brand = nested.get("brand");
            Object price = nested.get("unitPrice");
            return name + (brand != null ? " by " + brand : "") + (price != null ? " ($" + price + ")" : "");
        }
        // Generic fallback: join key=value pairs
        return nested.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(java.util.stream.Collectors.joining(", "));
    }

    private Object readableList(String key, List<?> list) {
        if (list.isEmpty()) return "";
        // Detect date-like list: [year, month, day] or [year, month, day, hour, min]
        if (list.size() >= 3 && list.get(0) instanceof Integer) {
            try {
                int year = (Integer) list.get(0);
                int month = (Integer) list.get(1);
                int day = (Integer) list.get(2);
                return String.format("%04d-%02d-%02d", year, month, day);
            } catch (Exception ignored) {}
        }
        return list.toString();
    }
}

