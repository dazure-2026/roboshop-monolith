package com.roboshop.controller;

import com.roboshop.model.User;
import com.roboshop.service.CartService;
import com.roboshop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final UserService userService;
    private final CartService cartService;

    public UserController(UserService userService, CartService cartService) {
        this.userService = userService;
        this.cartService = cartService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String redirect, Model model) {
        model.addAttribute("redirect", redirect);
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password,
                         @RequestParam(required = false) String redirect,
                         HttpSession session, RedirectAttributes redirectAttributes) {
        return userService.login(username, password)
                .map(user -> {
                    String oldSessionId = session.getId();
                    session.setAttribute("userId", user.getId());
                    session.setAttribute("username", user.getUsername());
                    // Merge anonymous cart into user cart
                    cartService.mergeSessionCartToUser(oldSessionId, user);
                    if (redirect != null && !redirect.isEmpty()) {
                        return "redirect:" + redirect;
                    }
                    return "redirect:/";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Invalid username or password");
                    return "redirect:/login";
                });
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String email,
                           @RequestParam String password, @RequestParam String firstName,
                           @RequestParam String lastName, @RequestParam(required = false) String phone,
                           RedirectAttributes redirectAttributes) {
        try {
            userService.register(username, email, password, firstName, lastName, phone);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        userService.findById(userId).ifPresent(user -> model.addAttribute("user", user));
        return "profile";
    }
}
