package com.ideiasmidias.security.model;

import com.ideiasmidias.adminuser.entity.AdminUser;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class AdminUserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final Boolean isActive;
    private final String role;

    public AdminUserPrincipal(AdminUser adminUser) {
        this.id = adminUser.getId();
        this.email = adminUser.getEmail();
        this.password = adminUser.getPasswordHash();
        this.isActive = adminUser.getIsActive();
        this.role = adminUser.getRole().name();
    }

    @Override
    public Collection<SimpleGrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(isActive);
    }
}