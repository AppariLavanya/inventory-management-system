package com.example.inventory.dto;

import java.time.Instant;
import java.util.List;

public class OrderResponseDto {
    public Long id;
    public String customerName;
    public String customerEmail;
    public Double total;
    public String status;
    public Instant createdAt;
    public List<Item> items;

    public static class Item {
        public Long id;
        public Long productId;
        public String productName;
        public Double unitPrice;
        public Integer quantity;
    }
}


