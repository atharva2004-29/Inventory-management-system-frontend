package com.example.frontend_orderinventory.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;

@Controller
public class HomeController {

    /**
     * Root: redirect logged-in users to dashboard, others to login.
     */
    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {
            return "redirect:/dashboard";
        }
        return "redirect:/login";
    }

    /**
     * Unified dashboard — shows team member cards for both CUSTOMER and ADMIN.
     * Passes isAdmin flag so templates can show/hide write-action buttons.
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("username", authentication != null ? authentication.getName() : "Guest");
        return "dashboard";
    }

    /**
     * Member API detail page — same for both roles, isAdmin controls button visibility.
     */
    @GetMapping("/member/{slug}")
    public String memberDetails(@PathVariable String slug, Authentication authentication, Model model) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);

        String name;
        List<Map<String, Object>> services = new ArrayList<>();

        switch (slug) {
            case "atharva-dhemare":
                name = "Atharva Dhemare";
                services.add(createServiceData("Customer", "/customers"));
                services.add(createServiceData("Product", "/products"));
                break;
            case "rajnandini-bawne":
                name = "Rajnandini Bawne";
                services.add(createServiceData("Order", "/orders"));
                break;
            case "chetna-bendale":
                name = "Chetna Bendale";
                services.add(createServiceData("Store", "/stores"));
                break;
            case "yash-mukkawar":
                name = "Yash Mukkawar";
                services.add(createServiceData("Shipment", "/shipments"));
                break;
            case "ranjeet-patil":
                name = "Ranjeet Patil";
                services.add(createServiceData("Inventory", "/inventory"));
                break;
            default:
                return "redirect:/dashboard";
        }

        model.addAttribute("memberName", name);
        model.addAttribute("services", services);
        return "member-details";
    }

    private Map<String, Object> createServiceData(String serviceName, String url) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", serviceName);
        data.put("url", url);
        data.put("apis", generateMockApis(serviceName, url));
        return data;
    }

    private List<Map<String, String>> generateMockApis(String service, String uiBase) {
        String base = "/api/" + service.toLowerCase();
        String viewBase = "/view/api/" + service.toLowerCase();

        if (service.equals("Inventory")) {
            base = "/api/inventory";
            viewBase = "/view/api/inventory";
        } else if (!base.endsWith("s")) {
            base = "/api/" + service.toLowerCase() + "s";
            viewBase = "/view/api/" + service.toLowerCase() + "s";
        }

        List<Map<String, String>> apis = new ArrayList<>();

        apis.add(createApi("GET", base, "List all " + service + "s", viewBase, null));
        apis.add(createApi("GET", base + "/{id}", "Get " + service + " details (Dynamic ID)", null, viewBase));
        apis.add(createApi("POST", base, "Create a new " + service, uiBase + "/new", null));
        apis.add(createApi("PUT", base + "/{id}", "Update existing " + service + " (Mock Link)", uiBase, null));
        apis.add(createApi("GET", base + "/search?name=test", "Search " + service + "s", viewBase + "/search", null));
        apis.add(createApi("PATCH", base + "/{id}/status", "Update " + service + " status", uiBase, null));

        return apis;
    }

    private Map<String, String> createApi(String method, String uri, String desc, String uiLink, String uiLinkBase) {
        Map<String, String> api = new HashMap<>();
        api.put("method", method);
        api.put("uri", uri);
        api.put("description", desc);
        if (uiLink != null) api.put("uiLink", uiLink);
        if (uiLinkBase != null) api.put("uiLinkBase", uiLinkBase);
        return api;
    }
}
