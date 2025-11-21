package com.example.inventory.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderDTO {
    private String customerName;
    private String customerEmail;
    private List<OrderItemDTO> items;
}



