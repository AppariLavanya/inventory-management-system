package com.example.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tokenType;
    private String email;

    public AuthResponse(String token, String email) {
        this.token = token;
        this.tokenType = "Bearer";
        this.email = email;
    }
}



