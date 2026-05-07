package com.roboshop.controller.api;

import com.roboshop.model.Cart;
import com.roboshop.model.User;
import com.roboshop.service.CartService;
import com.roboshop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class ApiCartController {

    private final CartService cartService;
    private final UserService userService;

    public ApiCartController(CartService cartService, UserService userService) {
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

    @GetMapping
    public Map<String, Object> getCart(HttpSession session) {
        User user = getCurrentUser(session);
        Cart cart = cartService.getOrCreateCart(user, session.getId());
        return Map.of(
                "items", cart.getItems(),
                "total", cartService.getCartTotal(cart),
                "itemCount", cartService.getCartItemCount(cart)
        );
    }

    @PostMapping("/add")
    public Map<String, Object> addToCart(@RequestBody Map<String, Object> body, HttpSession session) {
        User user = getCurrentUser(session);
        Long productId = Long.valueOf(body.get("productId").toString());
        int quantity = body.containsKey("quantity") ? Integer.parseInt(body.get("quantity").toString()) : 1;
        Cart cart = cartService.addToCart(user, session.getId(), productId, quantity);
        return Map.of("status", "ok", "itemCount", cartService.getCartItemCount(cart));
    }

    @DeleteMapping("/item/{itemId}")
    public Map<String, String> removeItem(@PathVariable Long itemId, HttpSession session) {
        User user = getCurrentUser(session);
        cartService.removeFromCart(user, session.getId(), itemId);
        return Map.of("status", "ok");
    }
}
