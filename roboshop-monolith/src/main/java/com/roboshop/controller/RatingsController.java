package com.roboshop.controller;

import com.roboshop.model.Product;
import com.roboshop.model.User;
import com.roboshop.service.CatalogueService;
import com.roboshop.service.RatingsService;
import com.roboshop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RatingsController {

    private final RatingsService ratingsService;
    private final UserService userService;
    private final CatalogueService catalogueService;

    public RatingsController(RatingsService ratingsService, UserService userService, CatalogueService catalogueService) {
        this.ratingsService = ratingsService;
        this.userService = userService;
        this.catalogueService = catalogueService;
    }

    @PostMapping("/ratings/add")
    public String addRating(@RequestParam Long productId, @RequestParam int score,
                            @RequestParam(required = false) String review,
                            HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findById(userId).orElseThrow();
        Product product = catalogueService.getProductById(productId).orElseThrow();
        ratingsService.addRating(product, user, score, review);
        redirectAttributes.addFlashAttribute("success", "Rating submitted!");
        return "redirect:/product/" + productId;
    }
}
