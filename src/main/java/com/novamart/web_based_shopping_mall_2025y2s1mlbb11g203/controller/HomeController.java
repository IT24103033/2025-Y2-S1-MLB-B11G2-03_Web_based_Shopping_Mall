package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index"; //returns index.html
    }
}