package com.example.frontend_orderinventory.controller;

import com.example.frontend_orderinventory.dto.OrderDTO;
import com.example.frontend_orderinventory.service.CustomerApiClient;
import com.example.frontend_orderinventory.service.OrderApiClient;
import com.example.frontend_orderinventory.service.StoreApiClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderApiClient orderApiClient;
    private final CustomerApiClient customerApiClient;
    private final StoreApiClient storeApiClient;

    public OrderController(OrderApiClient orderApiClient, CustomerApiClient customerApiClient, StoreApiClient storeApiClient) {
        this.orderApiClient = orderApiClient;
        this.customerApiClient = customerApiClient;
        this.storeApiClient = storeApiClient;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String status, Model model) {
        try {
            if (status != null && !status.isBlank()) {
                model.addAttribute("orders", orderApiClient.findByStatus(status));
                model.addAttribute("selectedStatus", status);
            } else {
                model.addAttribute("orders", orderApiClient.findAll());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Could not load orders: " + e.getMessage());
        }
        return "orders/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        try {
            model.addAttribute("order", new OrderDTO());
            model.addAttribute("formTitle", "Add Order");
            model.addAttribute("customers", customerApiClient.findAll());
            model.addAttribute("stores", storeApiClient.findAll());
        } catch (Exception e) {
            model.addAttribute("error", "Error loading form data: " + e.getMessage());
        }
        return "orders/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        try {
            model.addAttribute("order", orderApiClient.findById(id));
            model.addAttribute("formTitle", "Edit Order");
            model.addAttribute("customers", customerApiClient.findAll());
            model.addAttribute("stores", storeApiClient.findAll());
        } catch (Exception e) {
            model.addAttribute("error", "Could not load order record: " + e.getMessage());
            return "redirect:/orders";
        }
        return "orders/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute OrderDTO order,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        try {
            orderApiClient.save(order);
            redirectAttributes.addFlashAttribute("successMessage", "Order saved successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Error saving order: " + e.getMessage());
            model.addAttribute("formTitle", order.getOrderId() == null ? "Add Order" : "Edit Order");
            try {
                model.addAttribute("customers", customerApiClient.findAll());
                model.addAttribute("stores", storeApiClient.findAll());
            } catch (Exception ex) {}
            return "orders/form";
        }
        return "redirect:/orders";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            orderApiClient.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Order deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting order: " + e.getMessage());
        }
        return "redirect:/orders";
    }
}
