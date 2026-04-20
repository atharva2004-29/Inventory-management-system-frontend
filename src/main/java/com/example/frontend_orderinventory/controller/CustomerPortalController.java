package com.example.frontend_orderinventory.controller;


import com.example.frontend_orderinventory.dto.CustomerDTO;
import com.example.frontend_orderinventory.service.CustomerApiClient;
import com.example.frontend_orderinventory.service.OrderApiClient;
import com.example.frontend_orderinventory.service.ProductApiClient;
import com.example.frontend_orderinventory.service.ShipmentApiClient;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customer")
public class CustomerPortalController {

    private final CustomerApiClient customerApiClient;
    private final OrderApiClient orderApiClient;
    private final ShipmentApiClient shipmentApiClient;
    private final ProductApiClient productApiClient;

    public CustomerPortalController(CustomerApiClient customerApiClient, OrderApiClient orderApiClient, 
                                    ShipmentApiClient shipmentApiClient, ProductApiClient productApiClient) {
        this.customerApiClient = customerApiClient;
        this.orderApiClient = orderApiClient;
        this.shipmentApiClient = shipmentApiClient;
        this.productApiClient = productApiClient;
    }

    // Helper to get logged-in user email from Spring Security
    private String getEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            CustomerDTO customer = customerApiClient.findByEmail(getEmail());
            model.addAttribute("customer", customer);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Could not load profile.");
        }
        return "customer/dashboard";
    }

    @GetMapping("/orders")
    public String getOrders(Model model) {
        try {
            CustomerDTO customer = customerApiClient.findByEmail(getEmail());
            model.addAttribute("orders", orderApiClient.findByCustomerId(customer.getCustomerId()));
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Could not load orders.");
        }
        return "customer/orders";
    }

    @GetMapping("/shipments")
    public String getShipments(Model model) {
        try {
            CustomerDTO customer = customerApiClient.findByEmail(getEmail());
            model.addAttribute("shipments", shipmentApiClient.findByCustomerId(customer.getCustomerId()));
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Could not load shipments.");
        }
        return "customer/shipments";
    }

    @GetMapping("/products")
    public String getProducts(Model model) {
        try {
            model.addAttribute("products", productApiClient.findAll());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Could not load products.");
        }
        return "customer/products";
    }
}