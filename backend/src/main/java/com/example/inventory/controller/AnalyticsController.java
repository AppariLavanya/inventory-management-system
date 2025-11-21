package com.example.inventory.controller;

import com.example.inventory.service.AnalyticsService;
import com.example.inventory.service.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:3000")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final ProductService productService;

    public AnalyticsController(AnalyticsService analyticsService, ProductService productService) {
        this.analyticsService = analyticsService;
        this.productService = productService;
    }

    // MAIN SUMMARY
    @GetMapping("/summary")
    public ResponseEntity<?> summary() {
        return ResponseEntity.ok(analyticsService.getAnalyticsSummary());
    }

    // DAILY SALES GRAPH
    @GetMapping("/sales-daily")
    public ResponseEntity<?> salesDaily() {
        return ResponseEntity.ok(analyticsService.getAnalyticsDailySales());
    }

    // ⭐ FIXED LOW STOCK — uses ProductService.lowStock()
    @GetMapping("/low-stock")
    public ResponseEntity<?> lowStock(@RequestParam(defaultValue = "5") int threshold) {

        Map<String, Object> result = new HashMap<>();
        result.put("threshold", threshold);
        result.put("items", productService.lowStock(threshold)); // FINAL FIX

        return ResponseEntity.ok(result);
    }

    // LOW STOCK SUMMARY (COUNT ONLY)
    @GetMapping("/low-stock/summary")
    public ResponseEntity<?> lowStockSummary(@RequestParam(defaultValue = "5") int threshold) {
        return ResponseEntity.ok(analyticsService.getAnalyticsLowStockSummary(threshold));
    }
}





