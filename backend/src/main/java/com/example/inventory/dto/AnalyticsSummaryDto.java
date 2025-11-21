// src/main/java/com/example/inventory/dto/AnalyticsSummaryDto.java
package com.example.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsSummaryDto {

    private long totalProducts;
    private long totalOrders;
    private double totalRevenue;
    private long lowStockCount;

    private Map<String, Long> categoryCounts;

    private List<PriceSegmentDto> priceSegments;
    private List<TopProductDto> topProducts;
    private List<MonthlyRevenueDto> monthlyRevenue;
}








