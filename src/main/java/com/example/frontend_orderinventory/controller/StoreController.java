package com.example.frontend_orderinventory.controller;

import com.example.frontend_orderinventory.dto.StoreDTO;
import com.example.frontend_orderinventory.service.StoreApiClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/stores")
public class StoreController {

    private final StoreApiClient storeApiClient;

    public StoreController(StoreApiClient storeApiClient) {
        this.storeApiClient = storeApiClient;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        try {
            if (search != null && !search.isBlank()) {
                model.addAttribute("stores", storeApiClient.searchByName(search));
                model.addAttribute("search", search);
            } else {
                model.addAttribute("stores", storeApiClient.findAll());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Could not load stores: " + e.getMessage());
        }
        return "stores/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("store", new StoreDTO());
        model.addAttribute("formTitle", "Add Store");
        return "stores/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        try {
            model.addAttribute("store", storeApiClient.findById(id));
            model.addAttribute("formTitle", "Edit Store");
        } catch (Exception e) {
            model.addAttribute("error", "Could not load store record: " + e.getMessage());
            return "redirect:/stores";
        }
        return "stores/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute StoreDTO store,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        try {
            storeApiClient.save(store);
            redirectAttributes.addFlashAttribute("successMessage", "Store saved successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Error saving store: " + e.getMessage());
            model.addAttribute("formTitle", store.getStoreId() == null ? "Add Store" : "Edit Store");
            return "stores/form";
        }
        return "redirect:/stores";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            storeApiClient.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Store deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting store: " + e.getMessage());
        }
        return "redirect:/stores";
    }
}
