package com.letocart.java_apirest_2026.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "API REST Java 2026 est démarrée ! Essayez /api/products, /api/accounts, /api/orders, /api/notices";
    }

    @GetMapping("/status")
    public String status() {
        return "L'application fonctionne correctement !";
    }
}
