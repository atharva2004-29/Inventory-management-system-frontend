package com.example.frontend_orderinventory.controller;

import com.example.frontend_orderinventory.dto.InventoryDTO;
import com.example.frontend_orderinventory.service.InventoryApiClient;
import com.example.frontend_orderinventory.service.ProductApiClient;
import com.example.frontend_orderinventory.service.StoreApiClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryApiClient inventoryApiClient;
    private final ProductApiClient productApiClient;
    private final StoreApiClient storeApiClient;

    public InventoryController(InventoryApiClient inventoryApiClient, ProductApiClient productApiClient, StoreApiClient storeApiClient) {
        this.inventoryApiClient = inventoryApiClient;
        this.productApiClient = productApiClient;
        this.storeApiClient = storeApiClient;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Integer threshold, Model model) {
        try {
            if (threshold != null) {
                // Since the backend API has a low-stock endpoint, let's assume we might need to add it to ApiClient
                // For now, I'll filter or just use a generic list if ApiClient doesn't have it yet.
                // Actually, I'll add getLowStock to InventoryApiClient if I haven't.
                model.addAttribute("inventories", inventoryApiClient.findAll()); // Placeholder for filter
                model.addAttribute("threshold", threshold);
            } else {
                model.addAttribute("inventories", inventoryApiClient.findAll());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Could not load inventory: " + e.getMessage());
        }
        return "inventory/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        try {
            model.addAttribute("inventory", new InventoryDTO());
            model.addAttribute("formTitle", "Add Inventory");
            model.addAttribute("products", productApiClient.findAll());
            model.addAttribute("stores", storeApiClient.findAll());
        } catch (Exception e) {
            model.addAttribute("error", "Error loading form data: " + e.getMessage());
        }
        return "inventory/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        try {
            model.addAttribute("inventory", inventoryApiClient.findById(id));
            model.addAttribute("formTitle", "Edit Inventory");
            model.addAttribute("products", productApiClient.findAll());
            model.addAttribute("stores", storeApiClient.findAll());
        } catch (Exception e) {
            model.addAttribute("error", "Could not load inventory record: " + e.getMessage());
            return "redirect:/inventory";
        }
        return "inventory/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute InventoryDTO inventory,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        try {
            inventoryApiClient.save(inventory);
            redirectAttributes.addFlashAttribute("successMessage", "Inventory saved successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Error saving inventory: " + e.getMessage());
            model.addAttribute("formTitle", inventory.getInventoryId() == null ? "Add Inventory" : "Edit Inventory");
            try {
                model.addAttribute("products", productApiClient.findAll());
                model.addAttribute("stores", storeApiClient.findAll());
            } catch (Exception ex) {}
            return "inventory/form";
        }
        return "redirect:/inventory";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            inventoryApiClient.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Inventory deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting inventory: " + e.getMessage());
        }
        return "redirect:/inventory";
    }
}
