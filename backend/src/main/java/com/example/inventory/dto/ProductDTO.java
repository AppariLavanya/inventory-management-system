package com.example.inventory.dto;

import lombok.Data;

@Data
public class ProductDTO {

    private String sku;
    private String name;
    private String category;
    private String brand;
    private Integer stock;
    private Double price;

    // ⭐ NEW FIELD (Fixes getReorderLevel() errors)
    private Integer reorderLevel;
}





