package com.roboshop.controller;

import com.roboshop.service.CatalogueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    private final CatalogueService catalogueService;

    public HomeController(CatalogueService catalogueService) {
        this.catalogueService = catalogueService;
    }

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("featuredProducts", catalogueService.getAllProducts());
        return "home";
    }

    @RequestMapping("/health")
    @org.springframework.web.bind.annotation.ResponseBody
    public String health() {
        return "{\"status\":\"OK\"}";
    }
}
