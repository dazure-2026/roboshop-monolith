package com.roboshop.controller.api;

import com.roboshop.model.Order;
import com.roboshop.model.User;
import com.roboshop.service.OrderService;
import com.roboshop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class ApiOrderController {

    private final OrderService orderService;
    private final UserService userService;

    public ApiOrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> body, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("status", "error", "message", "Not logged in"));
        }
        User user = userService.findById(userId).orElseThrow();
        Long cityId = Long.valueOf(body.get("cityId").toString());
        try {
            Order order = orderService.createOrder(user, null, cityId);
            return ResponseEntity.ok(Map.of("status", "ok", "orderId", order.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserOrders(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("status", "error", "message", "Not logged in"));
        }
        User user = userService.findById(userId).orElseThrow();
        return ResponseEntity.ok(orderService.getUserOrders(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
