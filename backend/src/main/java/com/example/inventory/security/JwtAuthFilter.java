package com.example.inventory.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthFilter(JwtUtils jwtUtils, CustomUserDetailsService uds) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = uds;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String header = req.getHeader("Authorization");

            if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
                String token = header.substring(7);

                if (jwtUtils.isValid(token)) {
                    String email = jwtUtils.getEmailFromToken(token);

                    var userDetails = userDetailsService.loadUserByUsername(email);

                    var authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(req)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            // Clear security context on any parsing/auth error — do not throw further
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(req, res);
    }
}




