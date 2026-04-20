package com.example.frontend_orderinventory.controller;

import com.example.frontend_orderinventory.dto.ShipmentDTO;
import com.example.frontend_orderinventory.service.CustomerApiClient;
import com.example.frontend_orderinventory.service.ShipmentApiClient;
import com.example.frontend_orderinventory.service.StoreApiClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/shipments")
public class ShipmentController {

    private final ShipmentApiClient shipmentApiClient;
    private final StoreApiClient storeApiClient;
    private final CustomerApiClient customerApiClient;

    public ShipmentController(ShipmentApiClient shipmentApiClient, StoreApiClient storeApiClient, CustomerApiClient customerApiClient) {
        this.shipmentApiClient = shipmentApiClient;
        this.storeApiClient = storeApiClient;
        this.customerApiClient = customerApiClient;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String status, Model model) {
        try {
            if (status != null && !status.isBlank()) {
                model.addAttribute("shipments", shipmentApiClient.findByStatus(status));
                model.addAttribute("selectedStatus", status);
            } else {
                model.addAttribute("shipments", shipmentApiClient.findAll());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Could not load shipments: " + e.getMessage());
        }
        return "shipments/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        try {
            model.addAttribute("shipment", new ShipmentDTO());
            model.addAttribute("formTitle", "Add Shipment");
            model.addAttribute("stores", storeApiClient.findAll());
            model.addAttribute("customers", customerApiClient.findAll());
        } catch (Exception e) {
            model.addAttribute("error", "Error loading form data: " + e.getMessage());
        }
        return "shipments/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        try {
            model.addAttribute("shipment", shipmentApiClient.findById(id));
            model.addAttribute("formTitle", "Edit Shipment");
            model.addAttribute("stores", storeApiClient.findAll());
            model.addAttribute("customers", customerApiClient.findAll());
        } catch (Exception e) {
            model.addAttribute("error", "Could not load shipment record: " + e.getMessage());
            return "redirect:/shipments";
        }
        return "shipments/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute ShipmentDTO shipment,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        try {
            shipmentApiClient.save(shipment);
            redirectAttributes.addFlashAttribute("successMessage", "Shipment saved successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Error saving shipment: " + e.getMessage());
            model.addAttribute("formTitle", shipment.getShipmentId() == null ? "Add Shipment" : "Edit Shipment");
            try {
                model.addAttribute("stores", storeApiClient.findAll());
                model.addAttribute("customers", customerApiClient.findAll());
            } catch (Exception ex) {}
            return "shipments/form";
        }
        return "redirect:/shipments";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            shipmentApiClient.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Shipment deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting shipment: " + e.getMessage());
        }
        return "redirect:/shipments";
    }
}
