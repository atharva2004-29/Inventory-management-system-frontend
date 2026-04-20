package com.example.frontend_orderinventory.controller;

import com.example.frontend_orderinventory.dto.CustomerDTO;
import com.example.frontend_orderinventory.service.CustomerApiClient;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AuthController {

    private final CustomerApiClient customerApiClient;

    public AuthController(CustomerApiClient customerApiClient) {
        this.customerApiClient = customerApiClient;
    }

    /**
     * GET /login — Unified login page (two tabs: Customer & Admin)
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            @RequestParam(required = false) String registered,
                            Model model) {
        if (error != null) model.addAttribute("errorMessage", "Invalid credentials. Please try again.");
        if (logout != null) model.addAttribute("successMessage", "You have been logged out.");
        if (registered != null) model.addAttribute("successMessage", "Registration successful. You can now login.");
        return "auth/login";
    }

    /**
     * POST /customer-login — Customer email-only login (no password).
     * Programmatically assigns ROLE_CUSTOMER and stores auth in session.
     */
    @PostMapping("/customer-login")
    public String customerLogin(@RequestParam String email,
                                HttpServletRequest request) {
        if (email == null || email.trim().isEmpty()) {
            return "redirect:/login?error=true";
        }

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(email.trim(), null, authorities);

        SecurityContextHolder.getContext().setAuthentication(auth);

        HttpSession session = request.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        return "redirect:/dashboard";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new CustomerDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerCustomer(CustomerDTO customerDTO, Model model) {
        try {
            customerApiClient.save(customerDTO);
            return "redirect:/login?registered=true";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "auth/register";
        }
    }
}
