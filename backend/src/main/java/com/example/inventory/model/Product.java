package com.example.inventory.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(
        name = "products",
        indexes = {
                @Index(columnList = "name"),
                @Index(columnList = "category"),
                @Index(columnList = "sku", unique = true)
        }
)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "SKU is required")
    @Column(nullable = false, unique = true)
    private String sku;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    private String category;
    private String brand;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock = 0;

    @Min(value = 0, message = "Price cannot be negative")
    private Double price = 0.0;

    // NEW: persisted reorder level (can be null)
    @Column(name = "reorder_level")
    private Integer reorderLevel;

    // Transient computed fields (not saved)
    @Transient
    private String severity;        // CRITICAL / LOW / MEDIUM / UNKNOWN

    @Transient
    private Integer reorderSuggestion;  // reorderLevel - stock

    @Transient
    private Boolean reorderFlag;        // true when stock <= reorderLevel

    // Convenience constructor used by DataInitializer (keep it for compatibility)
    public Product(Long id, String sku, String name, String category, String brand, Integer stock, Double price) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.stock = stock;
        this.price = price;
    }

    @PrePersist
    @PreUpdate
    public void normalizeSku() {
        if (this.sku != null) {
            this.sku = this.sku.toUpperCase();
        }
    }
}






