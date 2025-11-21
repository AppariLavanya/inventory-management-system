package com.example.inventory.controller;

import com.example.inventory.model.Product;
import com.example.inventory.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/low-stock")
@CrossOrigin(origins = "http://localhost:3000")
public class LowStockController {

    private final ProductService service;

    public LowStockController(ProductService service) {
        this.service = service;
    }

    // support threshold query param
    @GetMapping
    public ResponseEntity<?> getLowStock(@RequestParam(required = false, defaultValue = "5") int threshold) {
        List<Product> list = service.lowStock(threshold);
        return ResponseEntity.ok(list);
    }
}

