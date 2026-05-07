package com.roboshop.controller;

import com.roboshop.model.Cart;
import com.roboshop.model.Order;
import com.roboshop.model.User;
import com.roboshop.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final ShippingService shippingService;
    private final UserService userService;

    public OrderController(OrderService orderService, CartService cartService,
                           ShippingService shippingService, UserService userService) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.shippingService = shippingService;
        this.userService = userService;
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findById(userId).orElseThrow();
        Cart cart = cartService.getOrCreateCart(user, null);
        if (cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("cart", cart);
        model.addAttribute("cartTotal", cartService.getCartTotal(cart));
        model.addAttribute("cities", shippingService.getAllCities());
        return "checkout";
    }

    @GetMapping("/payment")
    public String paymentPage(@RequestParam Long cityId, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findById(userId).orElseThrow();
        Cart cart = cartService.getOrCreateCart(user, null);
        if (cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }
        BigDecimal cartTotal = cartService.getCartTotal(cart);
        BigDecimal shippingCost = shippingService.calculateShippingCost(cityId);
        model.addAttribute("cart", cart);
        model.addAttribute("cartTotal", cartTotal);
        model.addAttribute("shippingCost", shippingCost);
        model.addAttribute("cityId", cityId);
        return "payment";
    }

    @PostMapping("/payment")
    public String processPayment(@RequestParam Long cityId,
                                 @RequestParam String cardName,
                                 @RequestParam String cardNumber,
                                 @RequestParam String expiry,
                                 @RequestParam String cvv,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findById(userId).orElseThrow();
        try {
            String maskedCard = "****" + cardNumber.replaceAll("\\s", "")
                    .substring(Math.max(0, cardNumber.replaceAll("\\s", "").length() - 4));
            Order order = orderService.createOrder(user, null, cityId, maskedCard);
            return "redirect:/order-confirmation/" + order.getId();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/checkout";
        }
    }

    @GetMapping("/order-confirmation/{id}")
    public String orderConfirmation(@PathVariable Long id, Model model) {
        orderService.getOrderById(id).ifPresent(order -> model.addAttribute("order", order));
        return "order-confirmation";
    }

    @GetMapping("/orders")
    public String orders(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findById(userId).orElseThrow();
        model.addAttribute("orders", orderService.getUserOrders(user));
        return "orders";
    }
}
