package com.example.inventory.service;

import com.example.inventory.dto.*;
import com.example.inventory.model.Order;
import com.example.inventory.model.OrderItem;
import com.example.inventory.model.Product;
import com.example.inventory.repository.OrderRepository;
import com.example.inventory.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;

    public AnalyticsService(ProductRepository productRepo, OrderRepository orderRepo) {
        this.productRepo = productRepo;
        this.orderRepo = orderRepo;
    }

    // ---------------------------------------------------------------------
    //  MAIN SUMMARY
    // ---------------------------------------------------------------------
    public AnalyticsSummaryDto summary() {

        long totalProducts = productRepo.count();
        long totalOrders = orderRepo.count();

        double totalRevenue = orderRepo.findAll().stream()
                .mapToDouble(o -> o.getTotal() == null ? 0.0 : o.getTotal())
                .sum();

        // FIX: use reorder_level for low-stock count
        long lowStockCount = productRepo.findAll().stream()
                .filter(p -> {
                    Integer stock = p.getStock();
                    Integer rl = p.getReorderLevel();
                    if (stock == null) return false;
                    if (rl == null) rl = 5;
                    return stock <= rl;
                })
                .count();

        // TOP PRODUCTS
        List<TopProductDto> topProducts = orderRepo.findAll().stream()
                .flatMap(o -> {
                    List<OrderItem> items = o.getItems();
                    return (items == null ? Collections.<OrderItem>emptyList() : items).stream();
                })
                .collect(Collectors.groupingBy(
                        OrderItem::getProductName,
                        Collectors.summingLong(i -> i.getQuantity() == null ? 0L : i.getQuantity())
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> new TopProductDto(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        // CATEGORY COUNTS
        Map<String, Long> categoryCounts = productRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCategory() == null ? "Unknown" : p.getCategory(),
                        Collectors.counting()
                ));

        // MONTHLY REVENUE
        Map<String, Double> monthlyMap = new LinkedHashMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM yyyy");
        ZoneId zone = ZoneId.systemDefault();

        for (Order o : orderRepo.findAll()) {
            if (o.getCreatedAt() == null) continue;
            String key = o.getCreatedAt().atZone(zone).format(fmt);
            double value = (o.getTotal() == null ? 0.0 : o.getTotal());
            monthlyMap.put(key, monthlyMap.getOrDefault(key, 0.0) + value);
        }

        List<MonthlyRevenueDto> monthlyList = monthlyMap.entrySet().stream()
                .map(e -> new MonthlyRevenueDto(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        AnalyticsSummaryDto dto = new AnalyticsSummaryDto();
        dto.setTotalProducts(totalProducts);
        dto.setTotalOrders(totalOrders);
        dto.setTotalRevenue(totalRevenue);
        dto.setLowStockCount(lowStockCount);
        dto.setTopProducts(topProducts);
        dto.setCategoryCounts(categoryCounts);
        dto.setMonthlyRevenue(monthlyList);

        return dto;
    }

    // ---------------------------------------------------------------------
    //  DAILY SALES
    // ---------------------------------------------------------------------
    public List<DailySalesDto> salesDaily() {

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM");
        ZoneId zone = ZoneId.systemDefault();

        return orderRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                        o -> o.getCreatedAt() == null ? "Unknown" :
                                o.getCreatedAt().atZone(zone).format(fmt),
                        Collectors.summingDouble(o -> o.getTotal() == null ? 0.0 : o.getTotal())
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new DailySalesDto(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------------------
    //  LOW STOCK SUMMARY (used by /analytics/low-stock)
    // ---------------------------------------------------------------------
    public Map<String, Object> lowStockSummary(int threshold) {

        List<Product> all = productRepo.findAll();

        // FIX: match ProductService logic (stock ≤ reorderLevel or ≤ threshold)
        List<Product> lowList = all.stream()
                .filter(p -> {
                    Integer stock = p.getStock();
                    if (stock == null) return false;

                    Integer rl = p.getReorderLevel();
                    if (rl == null) rl = 5;

                    // If user enters threshold, use it as limit
                    int finalLimit = Math.max(threshold, rl);

                    return stock <= finalLimit;
                })
                .collect(Collectors.toList());

        Map<String, Object> map = new HashMap<>();
        map.put("threshold", threshold);
        map.put("count", lowList.size());
        map.put("items", lowList);

        return map;
    }

    // WRAPPERS (for controller)
    public AnalyticsSummaryDto getAnalyticsSummary() {
        return summary();
    }

    public List<DailySalesDto> getAnalyticsDailySales() {
        return salesDaily();
    }

    public Map<String, Object> getAnalyticsLowStockSummary(int threshold) {
        return lowStockSummary(threshold);
    }
}























