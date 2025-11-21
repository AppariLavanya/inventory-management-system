package com.example.inventory.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class AnalyticsResponse {

    private long totalProducts;
    private long totalOrders;
    private double totalRevenue;

    private long lowStockCount;

    // category → count
    private Map<String, Long> categoryCounts;

    // price ranges (0–20000, 20000–50000, etc.)
    private List<PriceSegmentDto> priceSegments;

    // top selling products
    private List<TopProductDto> topProducts;

    // revenue for each month (yyyy-MM)
    private List<MonthlyRevenueDto> monthlyRevenue;

    // daily sales trend
    private List<DailySalesDto> dailySales;
}


