package com.example.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DailySalesDto {
    private String day;
    private Double revenue;
}


