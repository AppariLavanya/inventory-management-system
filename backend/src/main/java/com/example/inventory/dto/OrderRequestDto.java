package com.example.inventory.dto;

import java.util.List;

public class OrderRequestDto {
    public String customerName;
    public String customerEmail;
    public String status;
    public List<Item> items;

    public static class Item {
        public Long productId;
        public Integer quantity;
    }
}



