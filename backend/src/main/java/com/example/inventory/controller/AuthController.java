package com.example.inventory.controller;

import com.example.inventory.dto.AuthRequest;
import com.example.inventory.dto.AuthResponse;
import com.example.inventory.security.CustomUserDetails;
import com.example.inventory.security.CustomUserDetailsService;
import com.example.inventory.security.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authManager, JwtUtils jwtUtils) {
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getEmail(),
                        req.getPassword()
                )
        );

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        String token = jwtUtils.generateToken(
                user.getUsername(),
                user.getUserId(),
                user.getRoles()  // ENUM ROLES
        );

        return ResponseEntity.ok(
                new AuthResponse(token, "Bearer", user.getUsername())
        );
    }
}





