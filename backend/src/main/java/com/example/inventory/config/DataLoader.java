package com.example.inventory.config;

import com.example.inventory.model.Role;
import com.example.inventory.model.User;
import com.example.inventory.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!userRepo.existsByEmail("admin@gmail.com")) {

            User admin = new User();
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));

            admin.setRoles(Set.of(
                    Role.ROLE_ADMIN,
                    Role.ROLE_USER       // admin also behaves like a normal user
            ));

            userRepo.save(admin);

            System.out.println("✔ Admin created: admin@gmail.com / admin123");
        }

        if (!userRepo.existsByEmail("user@gmail.com")) {

            User user = new User();
            user.setEmail("user@gmail.com");
            user.setPassword(passwordEncoder.encode("user123"));

            user.setRoles(Set.of(
                    Role.ROLE_USER       // No admin access → can use exports normally
            ));

            userRepo.save(user);

            System.out.println("✔ User created: user@gmail.com / user123");
        }
    }
}




