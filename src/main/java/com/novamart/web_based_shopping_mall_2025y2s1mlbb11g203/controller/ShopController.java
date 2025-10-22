package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/shop")
public class ShopController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Placeholder stats; replace with real data later
        model.addAttribute("totalProducts", 0);
        model.addAttribute("totalOrders", 0);
        model.addAttribute("monthlyRevenue", 0);
        return "shop-dashboard";
    }
}


