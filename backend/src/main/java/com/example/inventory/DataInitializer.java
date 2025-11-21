package com.example.inventory;

import com.example.inventory.model.Product;
import com.example.inventory.model.Role;
import com.example.inventory.model.User;
import com.example.inventory.repository.ProductRepository;
import com.example.inventory.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            ProductRepository productRepo,
            UserRepository userRepo,
            PasswordEncoder passwordEncoder
    ) {
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        if (!userRepo.existsByEmail("admin@gmail.com")) {

            User admin = new User();
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.addRole(Role.ROLE_ADMIN);
            admin.addRole(Role.ROLE_USER);

            userRepo.save(admin);
            System.out.println("✔ Created ADMIN user → admin@gmail.com / admin123");
        }

        if (!userRepo.existsByEmail("user@gmail.com")) {

            User user = new User();
            user.setEmail("user@gmail.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.addRole(Role.ROLE_USER);

            userRepo.save(user);
            System.out.println("✔ Created USER account → user@gmail.com / user123");
        }

        // ======================================================
        //                DEFAULT PRODUCT DATA
        // ======================================================

        if (productRepo.count() == 0) {

            addProduct("SKU-ELEC001", "iPhone 15", "Electronics", "Apple", 50, 79999.0, 5);
            addProduct("SKU-ELEC002", "Samsung Galaxy S23", "Electronics", "Samsung", 60, 64999.0, 5);
            addProduct("SKU-ELEC003", "Sony Headphones", "Electronics", "Sony", 120, 9999.0, 5);
            addProduct("SKU-ELEC004", "Dell Laptop", "Electronics", "Dell", 30, 55000.0, 5);

            addProduct("SKU-FURN001", "Office Chair", "Furniture", "Duraflex", 20, 14999.0, 5);
            addProduct("SKU-FURN002", "Study Table", "Furniture", "WoodCraft", 10, 8999.0, 5);

            addProduct("SKU-HOME001", "Microwave Oven", "Home Appliances", "LG", 25, 12999.0, 5);
            addProduct("SKU-HOME002", "Air Conditioner", "Home Appliances", "Voltas", 18, 32999.0, 5);
            addProduct("SKU-HOME003", "Washing Machine", "Home Appliances", "Samsung", 15, 23999.0, 5);

            addProduct("SKU-CLOTH001", "Men's Jacket", "Clothing", "Roadster", 40, 1999.0, 5);
            addProduct("SKU-CLOTH002", "Women's Kurti", "Clothing", "Aurelia", 50, 1299.0, 5);
            addProduct("SKU-CLOTH003", "Men's Shoes", "Clothing", "Nike", 35, 4999.0, 5);

            addProduct("SKU-GAMING001", "PS5 Controller", "Electronics", "Sony", 50, 5999.0, 5);
            addProduct("SKU-GAMING002", "Gaming Mouse", "Electronics", "Logitech", 100, 2999.0, 5);
            addProduct("SKU-GAMING003", "Mechanical Keyboard", "Electronics", "Razer", 70, 8999.0, 5);

            System.out.println("✔ Default sample products loaded successfully!");
        }
    }
    private void addProduct(String sku, String name, String category, String brand,
                            int stock, double price, int reorderLevel) {

        Product p = new Product(null, sku, name, category, brand, stock, price);
        p.setReorderLevel(reorderLevel); // ⭐ NEW FIELD
        productRepo.save(p);
    }
}




