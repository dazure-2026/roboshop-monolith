package com.roboshop.config;

import com.roboshop.model.Cart;
import com.roboshop.service.CartService;
import com.roboshop.service.CatalogueService;
import com.roboshop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    private final CartService cartService;
    private final UserService userService;
    private final CatalogueService catalogueService;

    public GlobalModelAttributes(CartService cartService, UserService userService, CatalogueService catalogueService) {
        this.cartService = cartService;
        this.userService = userService;
        this.catalogueService = catalogueService;
    }

    @ModelAttribute
    public void addCommonAttributes(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            userService.findById(userId).ifPresent(user -> {
                model.addAttribute("currentUser", user);
                Cart cart = cartService.getOrCreateCart(user, null);
                model.addAttribute("cartItemCount", cartService.getCartItemCount(cart));
            });
        } else {
            Cart cart = cartService.getOrCreateCart(null, session.getId());
            model.addAttribute("cartItemCount", cartService.getCartItemCount(cart));
        }
        model.addAttribute("categories", catalogueService.getAllCategories());
    }
}
