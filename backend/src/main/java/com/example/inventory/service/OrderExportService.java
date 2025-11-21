// src/main/java/com/example/inventory/service/OrderExportService.java
package com.example.inventory.service;

import com.example.inventory.dto.AnalyticsSummaryDto;
import com.example.inventory.model.Order;
import com.example.inventory.model.Product;
import com.example.inventory.repository.OrderRepository;
import com.example.inventory.repository.ProductRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class OrderExportService {

    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;

    public OrderExportService(
            OrderRepository orderRepo,
            ProductRepository productRepo
    ) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
    }

    // ============================================================
    // LOCAL ANALYTICS SUMMARY (NO DEPENDENCY ON AnalyticsService)
    // ============================================================
    private AnalyticsSummaryDto buildSummary() {

        List<Order> orders = orderRepo.findAll();
        List<Product> products = productRepo.findAll();

        long totalProducts = products.size();
        long totalOrders = orders.size();

        double revenue = orders.stream()
                .mapToDouble(o -> o.getTotal() == null ? 0.0 : o.getTotal())
                .sum();

        long lowStock = products.stream()
                .filter(p -> p.getStock() != null && p.getReorderLevel() != null && p.getStock() <= p.getReorderLevel())
                .count();

        return AnalyticsSummaryDto.builder()
                .totalProducts(totalProducts)
                .totalOrders(totalOrders)
                .totalRevenue(revenue)
                .lowStockCount(lowStock)
                .build();
    }

    // ===================================================================
    // =============== COMBINED EXCEL EXPORT =============================
    // ===================================================================
    public ByteArrayInputStream exportExcel() throws Exception {

        Workbook wb = new XSSFWorkbook();

        // ====================== PRODUCTS SHEET ============================
        Sheet productSheet = wb.createSheet("Products");
        Row pHeader = productSheet.createRow(0);
        String[] pCols = {"ID", "SKU", "Name", "Category", "Brand", "Stock", "Price"};
        for (int i = 0; i < pCols.length; i++) pHeader.createCell(i).setCellValue(pCols[i]);

        List<Product> products = productRepo.findAll();
        int pRow = 1;
        for (Product p : products) {
            Row r = productSheet.createRow(pRow++);
            r.createCell(0).setCellValue(p.getId() == null ? 0L : p.getId());
            r.createCell(1).setCellValue(p.getSku() == null ? "" : p.getSku());
            r.createCell(2).setCellValue(p.getName() == null ? "" : p.getName());
            r.createCell(3).setCellValue(p.getCategory() == null ? "" : p.getCategory());
            r.createCell(4).setCellValue(p.getBrand() == null ? "" : p.getBrand());
            r.createCell(5).setCellValue(p.getStock() == null ? 0 : p.getStock());
            r.createCell(6).setCellValue(p.getPrice() == null ? 0.0 : p.getPrice());
        }

        // ====================== ORDERS SHEET ============================
        Sheet orderSheet = wb.createSheet("Orders");
        Row oHeader = orderSheet.createRow(0);
        String[] oCols = {"ID", "Customer", "Email", "Total", "Status", "Created At"};
        for (int i = 0; i < oCols.length; i++) oHeader.createCell(i).setCellValue(oCols[i]);

        List<Order> orders = orderRepo.findAll();
        int oRow = 1;
        for (Order o : orders) {
            Row r = orderSheet.createRow(oRow++);
            r.createCell(0).setCellValue(o.getId() == null ? 0L : o.getId());
            r.createCell(1).setCellValue(o.getCustomerName() == null ? "" : o.getCustomerName());
            r.createCell(2).setCellValue(o.getCustomerEmail() == null ? "" : o.getCustomerEmail());
            r.createCell(3).setCellValue(o.getTotal() == null ? 0.0 : o.getTotal());
            r.createCell(4).setCellValue(o.getStatus() == null ? "" : o.getStatus().name());
            r.createCell(5).setCellValue(o.getCreatedAt() == null ? "" : o.getCreatedAt().toString());
        }

        // ====================== LOW STOCK SHEET ============================
        Sheet lowSheet = wb.createSheet("Low Stock");
        Row lHeader = lowSheet.createRow(0);
        String[] lCols = {"ID", "Name", "Stock", "Reorder Level"};
        for (int i = 0; i < lCols.length; i++) lHeader.createCell(i).setCellValue(lCols[i]);

        int lRow = 1;
        for (Product p : products) {
            Integer stock = p.getStock();
            Integer rl = p.getReorderLevel();
            if (stock != null && rl != null && stock <= rl) {
                Row r = lowSheet.createRow(lRow++);
                r.createCell(0).setCellValue(p.getId() == null ? 0L : p.getId());
                r.createCell(1).setCellValue(p.getName() == null ? "" : p.getName());
                r.createCell(2).setCellValue(stock);
                r.createCell(3).setCellValue(rl);
            }
        }

        // ====================== ANALYTICS SUMMARY SHEET ============================
        Sheet aSheet = wb.createSheet("Analytics Summary");
        AnalyticsSummaryDto summary = buildSummary();

        aSheet.createRow(0).createCell(0).setCellValue("Total Products");
        aSheet.createRow(1).createCell(0).setCellValue("Total Orders");
        aSheet.createRow(2).createCell(0).setCellValue("Total Revenue");
        aSheet.createRow(3).createCell(0).setCellValue("Low Stock Items");

        aSheet.getRow(0).createCell(1).setCellValue(summary.getTotalProducts());
        aSheet.getRow(1).createCell(1).setCellValue(summary.getTotalOrders());
        aSheet.getRow(2).createCell(1).setCellValue(summary.getTotalRevenue());
        aSheet.getRow(3).createCell(1).setCellValue(summary.getLowStockCount());

        // OUTPUT
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        wb.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    // ===================================================================
    // ======================== COMBINED PDF EXPORT ======================
    // ===================================================================
    public ByteArrayInputStream exportPdf() {

        StringBuilder sb = new StringBuilder();

        sb.append("=========== INVENTORY SYSTEM REPORT ===========\n\n");

        // PRODUCTS
        sb.append("------ PRODUCTS ------\n");
        List<Product> products = productRepo.findAll();
        for (Product p : products) {
            sb.append(p.getId() == null ? "" : p.getId()).append(" | ")
                    .append(p.getName() == null ? "" : p.getName()).append(" | Stock: ").append(p.getStock() == null ? 0 : p.getStock())
                    .append(" | Price: ").append(p.getPrice() == null ? 0.0 : p.getPrice()).append("\n");
        }
        sb.append("\n\n");

        // ORDERS
        sb.append("------ ORDERS ------\n");
        List<Order> orders = orderRepo.findAll();
        for (Order o : orders) {
            sb.append(o.getId() == null ? "" : o.getId()).append(" | ")
                    .append(o.getCustomerName() == null ? "" : o.getCustomerName()).append(" | ")
                    .append(o.getTotal() == null ? 0.0 : o.getTotal()).append(" | ")
                    .append(o.getStatus() == null ? "" : o.getStatus().name()).append("\n");
        }
        sb.append("\n\n");

        // LOW STOCK
        sb.append("------ LOW STOCK ITEMS ------\n");
        for (Product p : products) {
            Integer stock = p.getStock();
            Integer rl = p.getReorderLevel();
            if (stock != null && rl != null && stock <= rl) {
                sb.append(p.getName() == null ? "" : p.getName()).append(" | Stock: ")
                        .append(stock).append("\n");
            }
        }
        sb.append("\n\n");

        // ANALYTICS
        AnalyticsSummaryDto summary = buildSummary();

        sb.append("------ ANALYTICS SUMMARY ------\n");
        sb.append("Total Products: ").append(summary.getTotalProducts()).append("\n");
        sb.append("Total Orders: ").append(summary.getTotalOrders()).append("\n");
        sb.append("Revenue: ").append(summary.getTotalRevenue()).append("\n");
        sb.append("Low Stock Items: ").append(summary.getLowStockCount()).append("\n");

        return new ByteArrayInputStream(sb.toString().getBytes());
    }
}




