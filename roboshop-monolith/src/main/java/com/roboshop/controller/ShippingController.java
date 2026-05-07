package com.roboshop.controller;

import com.roboshop.service.ShippingService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@Controller
public class ShippingController {

    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @GetMapping("/shipping/calc")
    @ResponseBody
    public Map<String, Object> calculateShipping(@RequestParam Long cityId) {
        BigDecimal cost = shippingService.calculateShippingCost(cityId);
        return Map.of("cityId", cityId, "shippingCost", cost);
    }
}
