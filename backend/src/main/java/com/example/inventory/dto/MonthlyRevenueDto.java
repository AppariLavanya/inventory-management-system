package com.example.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyRevenueDto {
    private String month;
    private Double revenue;
}



