package com.example.inventory.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopProductDto {
    private String productName;
    private Long quantitySold;
}


