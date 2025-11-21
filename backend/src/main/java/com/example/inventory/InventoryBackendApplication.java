package com.example.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InventoryBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryBackendApplication.class, args);
        System.out.println("🚀 Inventory Backend Started Successfully!");
    }
}


