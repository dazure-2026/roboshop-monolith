package com.roboshop.controller.api;

import com.roboshop.model.User;
import com.roboshop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class ApiUserController {

    private final UserService userService;

    public ApiUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        try {
            User user = userService.register(
                    body.get("username"), body.get("email"), body.get("password"),
                    body.get("firstName"), body.get("lastName"), body.get("phone")
            );
            return ResponseEntity.ok(Map.of("status", "ok", "userId", user.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body, HttpSession session) {
        return userService.login(body.get("username"), body.get("password"))
                .map(user -> {
                    session.setAttribute("userId", user.getId());
                    session.setAttribute("username", user.getUsername());
                    return ResponseEntity.ok(Map.of("status", "ok", "userId", (Object) user.getId()));
                })
                .orElseGet(() -> ResponseEntity.status(401)
                        .body(Map.of("status", "error", "message", (Object) "Invalid credentials")));
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> profile(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("status", "error", "message", "Not logged in"));
        }
        return userService.findById(userId)
                .map(user -> ResponseEntity.ok(Map.of(
                        "id", (Object) user.getId(),
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "firstName", user.getFirstName() != null ? user.getFirstName() : "",
                        "lastName", user.getLastName() != null ? user.getLastName() : ""
                )))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("status", "error", "message", "User not found")));
    }
}
