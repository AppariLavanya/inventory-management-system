package com.example.inventory.security;

import com.example.inventory.model.Role;
import com.example.inventory.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final String email;
    private final String password;

    private final Set<Role> roles;                   // Enum Roles
    private final Set<GrantedAuthority> authorities; // Spring authorities

    public CustomUserDetails(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.roles = user.getRoles() == null ? Set.of() : user.getRoles();

        // Convert roles → authorities ("ROLE_USER", "ROLE_ADMIN")
        this.authorities = this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());
    }

    /**
     * Return roles as strings, e.g. {"ROLE_USER", "ROLE_ADMIN"}.
     * Useful for token generation or checks.
     */
    public Set<String> getRolesAsString() {
        return roles.stream()
                .map(Role::name)
                .collect(Collectors.toSet());
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}




