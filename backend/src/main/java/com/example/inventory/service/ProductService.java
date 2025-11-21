package com.example.inventory.service;

import com.example.inventory.dto.AnalyticsSummaryDto;
import com.example.inventory.dto.MonthlyRevenueDto;
import com.example.inventory.dto.TopProductDto;
import com.example.inventory.dto.ProductDTO;
import com.example.inventory.model.Order;
import com.example.inventory.model.Product;
import com.example.inventory.repository.OrderRepository;
import com.example.inventory.repository.ProductRepository;
import com.example.inventory.spec.ProductSpecification;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Element;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.Image;

import java.io.ByteArrayOutputStream;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

@Service
public class ProductService {

    private final ProductRepository repo;
    private final OrderRepository orderRepo;
    private final AnalyticsService analyticsService;

    public ProductService(ProductRepository repo, OrderRepository orderRepo, AnalyticsService analyticsService) {
        this.repo = repo;
        this.orderRepo = orderRepo;
        this.analyticsService = analyticsService;
    }
    public Product create(ProductDTO dto) {
        Product p = new Product();
        p.setName(dto.getName());
        p.setCategory(dto.getCategory());
        p.setBrand(dto.getBrand());
        p.setPrice(dto.getPrice());
        p.setStock(dto.getStock());
        p.setReorderLevel(dto.getReorderLevel() == null ? 5 : dto.getReorderLevel());
        p.setSku(dto.getSku() == null || dto.getSku().isBlank()
                ? "SKU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()
                : dto.getSku());
        return repo.save(p);
    }

    public Product update(Long id, ProductDTO dto) {
        return repo.findById(id).map(p -> {
            p.setName(dto.getName());
            p.setCategory(dto.getCategory());
            p.setBrand(dto.getBrand());
            p.setPrice(dto.getPrice());
            p.setStock(dto.getStock());
            p.setReorderLevel(dto.getReorderLevel() == null ? 5 : dto.getReorderLevel());
            return repo.save(p);
        }).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public void deleteMany(List<Long> ids) {
        repo.deleteAllById(ids);
    }

    public Product get(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    // ---------------- Search / Pagination ----------------

    public Page<Product> search(
            String q,
            Double minPrice, Double maxPrice,
            Integer minStock, Integer maxStock,
            String category, String sort,
            int page, int size
    ) {
        Sort s = Sort.unsorted();

        if (sort != null && !sort.isBlank()) {
            try {
                String raw = sort.trim();
                if (raw.startsWith("-")) {
                    s = Sort.by(Sort.Order.desc(raw.substring(1)));
                } else if (raw.contains(",")) {
                    String[] parts = raw.split(",", 2);
                    s = "desc".equalsIgnoreCase(parts[1])
                            ? Sort.by(Sort.Order.desc(parts[0].trim()))
                            : Sort.by(Sort.Order.asc(parts[0].trim()));
                } else {
                    s = Sort.by(Sort.Order.asc(raw));
                }
            } catch (Exception ignored) {
            }
        }

        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), s);

        Specification<Product> spec = Specification.where(ProductSpecification.search(q))
                .and(ProductSpecification.priceBetween(minPrice, maxPrice))
                .and(ProductSpecification.stockBetween(minStock, maxStock))
                .and(ProductSpecification.categoryEquals(category));

        return repo.findAll(spec, pageable);
    }
    /**
     * Return products whose stock is less than or equal to the provided threshold.
     * This implementation is defensive:
     *  - ignores products with null stock
     *  - treats null reorderLevel as a configurable default (5)
     *  - computes severity / reorder flag / suggestion safely
     */
    public List<Product> lowStock(int threshold) {
        // sensible default
        if (threshold < 0) threshold = 5;

        List<Product> all = repo.findAll();
        List<Product> result = new ArrayList<>();

        for (Product p : all) {
            Integer stock = p.getStock();
            if (stock == null) {
                // If stock unknown, do not include it in low-stock list
                continue;
            }

            // If product stock <= threshold, include it
            if (stock <= threshold) {
                // compute reorderLevel safely (if null default to 5)
                int reorderLevel = p.getReorderLevel() == null ? 5 : p.getReorderLevel();

                // severity
                if (stock <= 2) {
                    p.setSeverity("CRITICAL");
                } else if (stock <= Math.max(1, threshold / 2)) {
                    p.setSeverity("LOW");
                } else {
                    p.setSeverity("MEDIUM");
                }

                // reorder flag & suggestion
                p.setReorderFlag(stock <= reorderLevel);
                p.setReorderSuggestion(Math.max(reorderLevel - stock, 0));

                result.add(p);
            }
        }

        return result;
    }
    public void exportExcel(HttpServletResponse response) throws Exception {

        try (Workbook wb = new XSSFWorkbook()) {

            // ---------- Products ----------
            Sheet productSheet = wb.createSheet("Products");
            Row pHeader = productSheet.createRow(0);
            String[] pCols = {"ID", "SKU", "Name", "Category", "Brand", "Stock", "ReorderLevel", "Price"};
            for (int i = 0; i < pCols.length; i++) pHeader.createCell(i).setCellValue(pCols[i]);

            List<Product> products = repo.findAll();
            int pRowIndex = 1;
            for (Product p : products) {
                Row r = productSheet.createRow(pRowIndex++);
                r.createCell(0).setCellValue(p.getId() == null ? 0L : p.getId());
                r.createCell(1).setCellValue(p.getSku() == null ? "" : p.getSku());
                r.createCell(2).setCellValue(p.getName() == null ? "" : p.getName());
                r.createCell(3).setCellValue(p.getCategory() == null ? "" : p.getCategory());
                r.createCell(4).setCellValue(p.getBrand() == null ? "" : p.getBrand());
                r.createCell(5).setCellValue(p.getStock() == null ? 0 : p.getStock());
                r.createCell(6).setCellValue(p.getReorderLevel() == null ? 0 : p.getReorderLevel());
                r.createCell(7).setCellValue(p.getPrice() == null ? 0.0 : p.getPrice());
            }
            for (int i = 0; i < pCols.length; i++) productSheet.autoSizeColumn(i);

            // ---------- Orders ----------
            Sheet orderSheet = wb.createSheet("Orders");
            Row oHeader = orderSheet.createRow(0);
            String[] oCols = {"ID", "Customer", "Email", "Total", "Status", "Created At"};
            for (int i = 0; i < oCols.length; i++) oHeader.createCell(i).setCellValue(oCols[i]);

            List<Order> orders = orderRepo.findAll();
            int oRowIndex = 1;
            for (Order o : orders) {
                Row r = orderSheet.createRow(oRowIndex++);
                r.createCell(0).setCellValue(o.getId() == null ? 0L : o.getId());
                r.createCell(1).setCellValue(o.getCustomerName() == null ? "" : o.getCustomerName());
                r.createCell(2).setCellValue(o.getCustomerEmail() == null ? "" : o.getCustomerEmail());
                r.createCell(3).setCellValue(o.getTotal() == null ? 0.0 : o.getTotal());
                r.createCell(4).setCellValue(o.getStatus() == null ? "" : o.getStatus().name());
                r.createCell(5).setCellValue(o.getCreatedAt() == null ? "" : o.getCreatedAt().toString());
            }
            for (int i = 0; i < oCols.length; i++) orderSheet.autoSizeColumn(i);

            // ---------- Low Stock ----------
            Sheet lowSheet = wb.createSheet("Low Stock");
            Row lHeader = lowSheet.createRow(0);
            String[] lCols = {"ID", "Name", "Stock", "Reorder Level", "Severity", "ReorderFlag", "Suggestion"};
            for (int i = 0; i < lCols.length; i++) lHeader.createCell(i).setCellValue(lCols[i]);

            int lRowIndex = 1;
            // Use computed lowStock with default threshold 5
            for (Product lowP : lowStock(5)) {
                Row r = lowSheet.createRow(lRowIndex++);
                r.createCell(0).setCellValue(lowP.getId() == null ? 0L : lowP.getId());
                r.createCell(1).setCellValue(lowP.getName() == null ? "" : lowP.getName());
                r.createCell(2).setCellValue(lowP.getStock() == null ? 0 : lowP.getStock());
                r.createCell(3).setCellValue(lowP.getReorderLevel() == null ? 0 : lowP.getReorderLevel());
                r.createCell(4).setCellValue(lowP.getSeverity() == null ? "" : lowP.getSeverity());
                r.createCell(5).setCellValue(lowP.getReorderFlag() == null ? false : lowP.getReorderFlag());
                r.createCell(6).setCellValue(lowP.getReorderSuggestion() == null ? 0 : lowP.getReorderSuggestion());
            }
            for (int i = 0; i < lCols.length; i++) lowSheet.autoSizeColumn(i);

            // ---------- Analytics (summary only) ----------
            Sheet aSheet = wb.createSheet("Analytics");
            // use wrapper method available on analyticsService
            AnalyticsSummaryDto summary = analyticsService.getAnalyticsSummary();

            int rowIdx = 0;
            aSheet.createRow(rowIdx).createCell(0).setCellValue("Analytics Summary");
            rowIdx++;
            aSheet.createRow(rowIdx).createCell(0).setCellValue("Total Products");
            aSheet.getRow(rowIdx++).createCell(1).setCellValue(summary.getTotalProducts());
            aSheet.createRow(rowIdx).createCell(0).setCellValue("Total Orders");
            aSheet.getRow(rowIdx++).createCell(1).setCellValue(summary.getTotalOrders());
            aSheet.createRow(rowIdx).createCell(0).setCellValue("Total Revenue");
            aSheet.getRow(rowIdx++).createCell(1).setCellValue(summary.getTotalRevenue());
            aSheet.createRow(rowIdx).createCell(0).setCellValue("Low Stock Count");
            aSheet.getRow(rowIdx++).createCell(1).setCellValue(summary.getLowStockCount());

            aSheet.autoSizeColumn(0);
            aSheet.autoSizeColumn(1);

            // ---------- Write workbook to response ----------
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=inventory_export.xlsx");
            wb.write(response.getOutputStream());
            response.flushBuffer();
        }
    }
    public void exportPDF(HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=inventory_export.pdf");

        Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(doc, response.getOutputStream());
        doc.open();

        Font h1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font h2 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font normal = FontFactory.getFont(FontFactory.HELVETICA, 10);

        // Title
        Paragraph title = new Paragraph("Inventory Export", h1);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);
        doc.add(Chunk.NEWLINE);

        // PRODUCTS table (basic)
        doc.add(new Paragraph("Products", h2));
        PdfPTable pt = new PdfPTable(4);
        pt.setWidthPercentage(100);
        pt.addCell("ID");
        pt.addCell("Name");
        pt.addCell("Stock");
        pt.addCell("Price");

        for (Product p : repo.findAll()) {
            pt.addCell(String.valueOf(p.getId() == null ? "" : p.getId()));
            pt.addCell(p.getName() == null ? "" : p.getName());
            pt.addCell(String.valueOf(p.getStock() == null ? 0 : p.getStock()));
            pt.addCell(String.valueOf(p.getPrice() == null ? 0.0 : p.getPrice()));
        }
        doc.add(pt);
        doc.add(Chunk.NEWLINE);

        // ORDERS table (basic)
        doc.add(new Paragraph("Orders", h2));
        PdfPTable ot = new PdfPTable(5);
        ot.setWidthPercentage(100);
        ot.addCell("ID");
        ot.addCell("Customer");
        ot.addCell("Email");
        ot.addCell("Total");
        ot.addCell("Status");

        for (Order o : orderRepo.findAll()) {
            ot.addCell(String.valueOf(o.getId() == null ? "" : o.getId()));
            ot.addCell(o.getCustomerName() == null ? "" : o.getCustomerName());
            ot.addCell(o.getCustomerEmail() == null ? "" : o.getCustomerEmail());
            ot.addCell(String.valueOf(o.getTotal() == null ? 0.0 : o.getTotal()));
            ot.addCell(o.getStatus() == null ? "" : o.getStatus().name());
        }
        doc.add(ot);
        doc.add(Chunk.NEWLINE);

        // LOW STOCK table
        doc.add(new Paragraph("Low Stock Items", h2));
        PdfPTable lt = new PdfPTable(4);
        lt.setWidthPercentage(100);
        lt.addCell("ID");
        lt.addCell("Name");
        lt.addCell("Stock");
        lt.addCell("Reorder Level");

        for (Product p : repo.findAll()) {
            int stock = p.getStock() == null ? 0 : p.getStock();
            int rl = p.getReorderLevel() == null ? 5 : p.getReorderLevel();
            if (stock <= rl) {
                lt.addCell(String.valueOf(p.getId() == null ? "" : p.getId()));
                lt.addCell(p.getName() == null ? "" : p.getName());
                lt.addCell(String.valueOf(stock));
                lt.addCell(String.valueOf(rl));
            }
        }
        doc.add(lt);
        doc.add(Chunk.NEWLINE);

        // ANALYTICS summary
        AnalyticsSummaryDto summary = analyticsService.getAnalyticsSummary();
        doc.add(new Paragraph("Analytics Summary", h2));

        PdfPTable sum = new PdfPTable(2);
        sum.setWidthPercentage(60);
        sum.addCell("Metric");
        sum.addCell("Value");

        sum.addCell("Total Products");
        sum.addCell(String.valueOf(summary.getTotalProducts()));

        sum.addCell("Total Orders");
        sum.addCell(String.valueOf(summary.getTotalOrders()));

        sum.addCell("Total Revenue");
        sum.addCell(String.valueOf(summary.getTotalRevenue()));

        sum.addCell("Low Stock Count");
        sum.addCell(String.valueOf(summary.getLowStockCount()));

        doc.add(sum);
        doc.add(Chunk.NEWLINE);

        // Generate charts using JFreeChart -> embed as images into PDF

        // 1) Monthly Revenue chart (bar)
        List<MonthlyRevenueDto> monthly = summary.getMonthlyRevenue();
        if (monthly != null && !monthly.isEmpty()) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (MonthlyRevenueDto m : monthly) {
                dataset.addValue(m.getRevenue(), "Revenue", m.getMonth());
            }
            JFreeChart barChart = ChartFactory.createBarChart(
                    "Monthly Revenue",
                    "Month",
                    "Revenue",
                    dataset,
                    PlotOrientation.VERTICAL,
                    false,
                    true,
                    false
            );
            BufferedImage chartImage = barChart.createBufferedImage(800, 300);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(chartImage, "png", baos);
            Image img = Image.getInstance(baos.toByteArray());
            img.setAlignment(Element.ALIGN_CENTER);
            img.scaleToFit(520, 250);
            doc.add(img);
            doc.add(Chunk.NEWLINE);
        }

        // 2) Category distribution chart (pie)
        Map<String, Long> categories = summary.getCategoryCounts();
        if (categories != null && !categories.isEmpty()) {
            DefaultPieDataset pd = new DefaultPieDataset();
            for (Map.Entry<String, Long> e : categories.entrySet()) {
                pd.setValue(e.getKey(), e.getValue());
            }
            JFreeChart pieChart = ChartFactory.createPieChart(
                    "Category Distribution",
                    pd,
                    true,
                    true,
                    false
            );
            BufferedImage pieImage = pieChart.createBufferedImage(600, 300);
            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
            ImageIO.write(pieImage, "png", baos2);
            Image pimg = Image.getInstance(baos2.toByteArray());
            pimg.setAlignment(Element.ALIGN_CENTER);
            pimg.scaleToFit(520, 250);
            doc.add(pimg);
            doc.add(Chunk.NEWLINE);
        }

        // 3) Top products chart (bar)
        List<TopProductDto> topProducts = summary.getTopProducts();
        if (topProducts != null && !topProducts.isEmpty()) {
            DefaultCategoryDataset topDs = new DefaultCategoryDataset();
            for (TopProductDto t : topProducts) {
                // quantitySold is likely Integer — JFreeChart accepts Number
                topDs.addValue(t.getQuantitySold(), "Qty", t.getProductName());
            }
            JFreeChart topChart = ChartFactory.createBarChart(
                    "Top Products",
                    "Product",
                    "Qty Sold",
                    topDs,
                    PlotOrientation.VERTICAL,
                    false,
                    true,
                    false
            );
            BufferedImage topImage = topChart.createBufferedImage(800, 300);
            ByteArrayOutputStream baos3 = new ByteArrayOutputStream();
            ImageIO.write(topImage, "png", baos3);
            Image timg = Image.getInstance(baos3.toByteArray());
            timg.setAlignment(Element.ALIGN_CENTER);
            timg.scaleToFit(520, 250);
            doc.add(timg);
            doc.add(Chunk.NEWLINE);
        }

        doc.close();
    }
}














