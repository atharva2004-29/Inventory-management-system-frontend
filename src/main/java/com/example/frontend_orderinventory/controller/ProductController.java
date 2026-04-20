package com.example.frontend_orderinventory.controller;

import com.example.frontend_orderinventory.dto.ProductDTO;
import com.example.frontend_orderinventory.service.ProductApiClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductApiClient productApiClient;

    public ProductController(ProductApiClient productApiClient) {
        this.productApiClient = productApiClient;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        try {
            if (search != null && !search.isBlank()) {
                model.addAttribute("products", productApiClient.searchByName(search));
                model.addAttribute("search", search);
            } else {
                model.addAttribute("products", productApiClient.findAll());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Could not load products: " + e.getMessage());
        }
        return "products/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("product", new ProductDTO());
        model.addAttribute("formTitle", "Add Product");
        return "products/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        try {
            model.addAttribute("product", productApiClient.findById(id));
            model.addAttribute("formTitle", "Edit Product");
        } catch (Exception e) {
            model.addAttribute("error", "Could not load product record: " + e.getMessage());
            return "redirect:/products";
        }
        return "products/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute ProductDTO product,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        try {
            productApiClient.save(product);
            redirectAttributes.addFlashAttribute("successMessage", "Product saved successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Error saving product: " + e.getMessage());
            model.addAttribute("formTitle", product.getProductId() == null ? "Add Product" : "Edit Product");
            return "products/form";
        }
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            productApiClient.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting product: " + e.getMessage());
        }
        return "redirect:/products";
    }
}
