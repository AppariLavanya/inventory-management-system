package com.example.inventory.service;

import com.example.inventory.dto.OrderRequestDto;
import com.example.inventory.dto.OrderResponseDto;
import com.example.inventory.model.Order;
import com.example.inventory.model.OrderItem;
import com.example.inventory.model.OrderStatus;
import com.example.inventory.repository.OrderRepository;
import com.example.inventory.repository.ProductRepository;
import com.example.inventory.spec.OrderSpecification;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;

    public OrderService(OrderRepository orderRepo, ProductRepository productRepo) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
    }
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto dto) {

        Order o = new Order();
        o.setCustomerName(dto.customerName);
        o.setCustomerEmail(dto.customerEmail);
        o.setStatus(dto.status != null ? OrderStatus.valueOf(dto.status) : OrderStatus.PENDING);
        o.setCreatedAt(Instant.now());

        double total = 0.0;

        if (dto.items != null) {
            for (OrderRequestDto.Item it : dto.items) {

                // Skip blank rows
                if (it.productId == null || it.productId <= 0) continue;

                var p = productRepo.findById(it.productId).orElse(null);

                OrderItem item = new OrderItem();
                item.setProductId(it.productId);
                item.setProductName(p != null ? p.getName() : "Unknown");
                item.setUnitPrice(p != null ? p.getPrice() : 0.0);
                item.setQuantity(it.quantity == null ? 1 : it.quantity);

                o.addItem(item);
                total += item.getUnitPrice() * item.getQuantity();
            }
        }

        o.setTotal(total);
        return toDto(orderRepo.save(o));
    }
    public OrderResponseDto get(Long id) {
        return orderRepo.findById(id)
                .map(this::toDto)
                .orElse(null);
    }
    @Transactional
    public OrderResponseDto update(Long id, OrderRequestDto dto) {

        Order o = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        o.setCustomerName(dto.customerName);
        o.setCustomerEmail(dto.customerEmail);

        if (dto.status != null)
            o.setStatus(OrderStatus.valueOf(dto.status));

        // Remove old items
        o.clearItems();

        double total = 0;

        if (dto.items != null) {

            for (OrderRequestDto.Item it : dto.items) {

                // Skip rows with empty productId or invalid quantity
                if (it.productId == null || it.productId <= 0) continue;
                if (it.quantity == null || it.quantity <= 0) continue;

                var p = productRepo.findById(it.productId).orElse(null);

                OrderItem item = new OrderItem();
                item.setProductId(it.productId);
                item.setProductName(p != null ? p.getName() : "Unknown");
                item.setUnitPrice(p != null ? p.getPrice() : 0.0);
                item.setQuantity(it.quantity);

                o.addItem(item);
                total += item.getUnitPrice() * item.getQuantity();
            }
        }

        o.setTotal(total);

        return toDto(orderRepo.save(o));
    }

    // ============================================================
    // UPDATE STATUS ONLY
    // ============================================================
    @Transactional
    public OrderResponseDto updateStatus(Long id, String status) {
        Order o = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        o.setStatus(OrderStatus.valueOf(status));
        return toDto(orderRepo.save(o));
    }
    @Transactional
    public void delete(Long id) {
        orderRepo.deleteById(id);
    }
    public Page<OrderResponseDto> listOrdersPaged(
            String customerName,
            Double minTotal,
            Double maxTotal,
            Instant createdAfter,
            Instant createdBefore,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {

        Sort sort = Sort.by(
                Sort.Direction.fromString(sortDir == null ? "DESC" : sortDir),
                sortBy == null ? "createdAt" : sortBy
        );

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Order> spec = Specification.where(null);

        if (customerName != null && !customerName.isBlank())
            spec = spec.and(OrderSpecification.customerNameContains(customerName));

        if (minTotal != null)
            spec = spec.and(OrderSpecification.minTotal(minTotal));

        if (maxTotal != null)
            spec = spec.and(OrderSpecification.maxTotal(maxTotal));

        if (createdAfter != null)
            spec = spec.and(OrderSpecification.createdAfter(createdAfter));

        if (createdBefore != null)
            spec = spec.and(OrderSpecification.createdBefore(createdBefore));

        return orderRepo.findAll(spec, pageable).map(this::toDto);
    }
    private OrderResponseDto toDto(Order o) {

        OrderResponseDto r = new OrderResponseDto();
        r.id = o.getId();
        r.customerName = o.getCustomerName();
        r.customerEmail = o.getCustomerEmail();
        r.total = o.getTotal();
        r.status = o.getStatus().name();
        r.createdAt = o.getCreatedAt();

        r.items = o.getItems().stream().map(i -> {
            OrderResponseDto.Item it = new OrderResponseDto.Item();
            it.id = i.getId();
            it.productId = i.getProductId();
            it.productName = i.getProductName();
            it.unitPrice = i.getUnitPrice();
            it.quantity = i.getQuantity();
            return it;
        }).collect(Collectors.toList());

        return r;
    }
}


















