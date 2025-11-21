package com.example.inventory.controller;

import com.example.inventory.dto.ProductDTO;
import com.example.inventory.model.Product;
import com.example.inventory.service.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }
    @GetMapping
    public ResponseEntity<?> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) Integer maxStock,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                service.search(q, minPrice, maxPrice, minStock, maxStock, category, sort, page, size)
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ProductDTO dto) {
        Product created = service.create(dto);
        return ResponseEntity.ok(created);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ProductDTO dto) {
        Product updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }
    @DeleteMapping
    public ResponseEntity<?> deleteMany(@RequestBody List<Long> ids) {
        service.deleteMany(ids);
        return ResponseEntity.ok("Deleted selected products");
    }
    @GetMapping("/export/excel")
    public void exportExcel(HttpServletResponse response) {
        try {
            service.exportExcel(response);
        } catch (Exception ex) {
            try {
                response.reset();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                PrintWriter w = response.getWriter();
                w.write("{\"error\":\"Failed to export Excel\"}");
                w.flush();
                response.flushBuffer();
            } catch (Exception ignored) {}
        }
    }
    @GetMapping("/export/pdf")
    public void exportPDF(HttpServletResponse response) {
        try {
            service.exportPDF(response);
        } catch (Exception ex) {
            try {
                response.reset();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                PrintWriter w = response.getWriter();
                w.write("{\"error\":\"Failed to export PDF\"}");
                w.flush();
                response.flushBuffer();
            } catch (Exception ignored) {}
        }
    }
}






















