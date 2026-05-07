package com.roboshop.controller;

import com.roboshop.model.Cart;
import com.roboshop.model.User;
import com.roboshop.service.CartService;
import com.roboshop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    private User getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            return userService.findById(userId).orElse(null);
        }
        return null;
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        User user = getCurrentUser(session);
        Cart cart = cartService.getOrCreateCart(user, session.getId());
        model.addAttribute("cart", cart);
        model.addAttribute("cartTotal", cartService.getCartTotal(cart));
        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            HttpSession session) {
        User user = getCurrentUser(session);
        cartService.addToCart(user, session.getId(), productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/update")
    public String updateCart(@RequestParam Long itemId,
                             @RequestParam int quantity,
                             HttpSession session) {
        User user = getCurrentUser(session);
        cartService.updateQuantity(user, session.getId(), itemId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long itemId, HttpSession session) {
        User user = getCurrentUser(session);
        cartService.removeFromCart(user, session.getId(), itemId);
        return "redirect:/cart";
    }
}
