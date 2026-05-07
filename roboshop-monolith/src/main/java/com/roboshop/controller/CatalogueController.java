package com.roboshop.controller;

import com.roboshop.service.CatalogueService;
import com.roboshop.service.RatingsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CatalogueController {

    private final CatalogueService catalogueService;
    private final RatingsService ratingsService;

    public CatalogueController(CatalogueService catalogueService, RatingsService ratingsService) {
        this.catalogueService = catalogueService;
        this.ratingsService = ratingsService;
    }

    @GetMapping("/catalogue")
    public String catalogue(@RequestParam(required = false) String category,
                            @RequestParam(required = false) String q,
                            Model model) {
        if (q != null && !q.isEmpty()) {
            model.addAttribute("products", catalogueService.searchProducts(q));
            model.addAttribute("searchQuery", q);
        } else if (category != null && !category.isEmpty()) {
            model.addAttribute("products", catalogueService.getProductsByCategory(category));
            model.addAttribute("selectedCategory", category);
        } else {
            model.addAttribute("products", catalogueService.getAllProducts());
        }
        return "catalogue";
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        catalogueService.getProductById(id).ifPresent(product -> {
            model.addAttribute("product", product);
            model.addAttribute("averageRating", ratingsService.getAverageRating(id));
            model.addAttribute("ratingCount", ratingsService.getRatingCount(id));
            model.addAttribute("ratings", ratingsService.getProductRatings(id));
        });
        return "product-detail";
    }
}
