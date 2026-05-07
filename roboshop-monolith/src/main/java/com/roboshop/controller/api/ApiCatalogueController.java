package com.roboshop.controller.api;

import com.roboshop.model.Product;
import com.roboshop.service.CatalogueService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/catalogue")
public class ApiCatalogueController {

    private final CatalogueService catalogueService;

    public ApiCatalogueController(CatalogueService catalogueService) {
        this.catalogueService = catalogueService;
    }

    @GetMapping("/products")
    public List<Product> getAllProducts(@RequestParam(required = false) String category) {
        if (category != null && !category.isEmpty()) {
            return catalogueService.getProductsByCategory(category);
        }
        return catalogueService.getAllProducts();
    }

    @GetMapping("/products/{id}")
    public Product getProduct(@PathVariable Long id) {
        return catalogueService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @GetMapping("/products/search")
    public List<Product> searchProducts(@RequestParam String q) {
        return catalogueService.searchProducts(q);
    }

    @GetMapping("/categories")
    public List<String> getCategories() {
        return catalogueService.getAllCategories();
    }
}
