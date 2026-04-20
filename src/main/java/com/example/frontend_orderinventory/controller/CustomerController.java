package com.example.frontend_orderinventory.controller;

import com.example.frontend_orderinventory.dto.CustomerDTO;
import com.example.frontend_orderinventory.service.CustomerApiClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerApiClient customerApiClient;

    public CustomerController(CustomerApiClient customerApiClient) {
        this.customerApiClient = customerApiClient;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        try {
            if (search != null && !search.isBlank()) {
                model.addAttribute("customers", customerApiClient.searchByName(search));
                model.addAttribute("search", search);
            } else {
                model.addAttribute("customers", customerApiClient.findAll());
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Could not load customers: " + e.getMessage());
        }
        return "customers/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("customer", new CustomerDTO());
        model.addAttribute("formTitle", "Add Customer");
        return "customers/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        try {
            model.addAttribute("customer", customerApiClient.findById(id));
            model.addAttribute("formTitle", "Edit Customer");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Could not load customer: " + e.getMessage());
            return "redirect:/customers";
        }
        return "customers/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute CustomerDTO customer,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        try {
            customerApiClient.save(customer);
            redirectAttributes.addFlashAttribute("successMessage", "Customer saved successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error saving customer: " + e.getMessage());
            model.addAttribute("formTitle", customer.getCustomerId() == null ? "Add Customer" : "Edit Customer");
            return "customers/form";
        }
        return "redirect:/customers";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            customerApiClient.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Customer deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting customer: " + e.getMessage());
        }
        return "redirect:/customers";
    }
}
