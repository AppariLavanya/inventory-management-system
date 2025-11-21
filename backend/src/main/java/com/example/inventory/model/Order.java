package com.example.inventory.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String customerEmail;
    private Double total = 0.0;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    // Auto timestamps
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void setUpdateTime() {
        updatedAt = Instant.now();
    }

    //  ***************************************
    //  FIXED: make fetch = FetchType.EAGER
    //  ***************************************
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER    // 🔥 THIS FIXES THE ERROR
    )
    private List<OrderItem> items = new ArrayList<>();

    public Order() {}

    // Sync helper
    public void addItem(OrderItem item) {
        item.setOrder(this);
        items.add(item);
    }

    public void clearItems() {
        items.forEach(i -> i.setOrder(null));
        items.clear();
    }

    // ============================
    //       STATUS VALIDATION
    // ============================
    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }

    // ============================
    //       GETTERS & SETTERS
    // ============================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) {
        clearItems();
        if (items != null) items.forEach(this::addItem);
    }
}





