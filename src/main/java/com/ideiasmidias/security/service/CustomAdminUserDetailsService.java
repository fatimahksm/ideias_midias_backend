package com.ideiasmidias.security.service;

import com.ideiasmidias.adminuser.entity.AdminUser;
import com.ideiasmidias.adminuser.repository.AdminUserRepository;
import com.ideiasmidias.security.model.AdminUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomAdminUserDetailsService implements UserDetailsService {

    private final AdminUserRepository adminUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AdminUser adminUser = adminUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Admin user not found with email: " + email));

        return new AdminUserPrincipal(adminUser);
    }
}