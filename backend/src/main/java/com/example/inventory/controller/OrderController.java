package com.example.inventory.controller;

import com.example.inventory.dto.OrderRequestDto;
import com.example.inventory.dto.OrderResponseDto;
import com.example.inventory.service.OrderService;
import com.example.inventory.service.OrderExportService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    private final OrderService orderService;
    private final OrderExportService exportService;

    public OrderController(OrderService orderService, OrderExportService exportService) {
        this.orderService = orderService;
        this.exportService = exportService;
    }
    @PostMapping
    public ResponseEntity<OrderResponseDto> create(@RequestBody OrderRequestDto dto) {
        OrderResponseDto created = orderService.createOrder(dto);
        return ResponseEntity.created(URI.create("/api/orders/" + created.id)).body(created);
    }
    @GetMapping
    public ResponseEntity<Page<OrderResponseDto>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "customerName", required = false) String customerName,
            @RequestParam(name = "customer", required = false) String customer,
            @RequestParam(required = false) Double minTotal,
            @RequestParam(required = false) Double maxTotal,
            @RequestParam(required = false) String createdAfter,
            @RequestParam(required = false) String createdBefore,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir
    ) {

        String finalCustomer = (customerName != null && !customerName.isBlank())
                ? customerName
                : (customer != null && !customer.isBlank() ? customer : null);

        Instant from = null, to = null;

        try {
            if (createdAfter != null && !createdAfter.isBlank())
                from = Instant.parse(createdAfter);
        } catch (DateTimeParseException ignore) {}

        try {
            if (createdBefore != null && !createdBefore.isBlank())
                to = Instant.parse(createdBefore);
        } catch (DateTimeParseException ignore) {}

        Page<OrderResponseDto> result = orderService.listOrdersPaged(
                finalCustomer,
                minTotal,
                maxTotal,
                from,
                to,
                page,
                size,
                sortBy,
                sortDir
        );

        return ResponseEntity.ok(result);
    }
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> get(@PathVariable Long id) {
        OrderResponseDto dto = orderService.get(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDto> update(@PathVariable Long id, @RequestBody OrderRequestDto dto) {
        return ResponseEntity.ok(orderService.update(id, dto));
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDto> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel() throws Exception {
        byte[] bytes = exportService.exportExcel().readAllBytes();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=inventory.xlsx")
                .body(bytes);
    }
    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf() throws Exception {
        byte[] bytes = exportService.exportPdf().readAllBytes();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=inventory.pdf")
                .body(bytes);
    }
}


















