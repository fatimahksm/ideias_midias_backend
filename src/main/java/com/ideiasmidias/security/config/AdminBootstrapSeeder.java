package com.ideiasmidias.security.config;

import com.ideiasmidias.adminuser.entity.AdminUser;
import com.ideiasmidias.adminuser.repository.AdminUserRepository;
import com.ideiasmidias.common.enums.AdminRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminBootstrapSeeder implements CommandLineRunner {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.enabled:false}")
    private boolean enabled;

    @Value("${app.bootstrap.admin.full-name:Super Admin}")
    private String fullName;

    @Value("${app.bootstrap.admin.email:admin@ideiasmidias.com}")
    private String email;

    @Value("${app.bootstrap.admin.password:Admin@123456}")
    private String rawPassword;

    @Override
    public void run(String... args) {
        if (!enabled) {
            log.info("Admin bootstrap seeding is disabled.");
            return;
        }

        String normalizedEmail = email == null ? null : email.trim().toLowerCase();

        if (normalizedEmail == null || normalizedEmail.isBlank()) {
            log.warn("Bootstrap admin email is blank. Seeder skipped.");
            return;
        }

        if (rawPassword == null || rawPassword.isBlank()) {
            log.warn("Bootstrap admin password is blank. Seeder skipped.");
            return;
        }

        if (adminUserRepository.existsByEmail(normalizedEmail)) {
            log.info("Bootstrap admin already exists: {}", normalizedEmail);
            return;
        }

        AdminUser adminUser = new AdminUser();
        adminUser.setFullName(fullName != null && !fullName.isBlank() ? fullName.trim() : "Super Admin");
        adminUser.setEmail(normalizedEmail);
        adminUser.setPasswordHash(passwordEncoder.encode(rawPassword));
        adminUser.setRole(AdminRole.SUPER_ADMIN);
        adminUser.setIsActive(true);

        adminUserRepository.save(adminUser);

        log.info("Bootstrap SUPER_ADMIN created successfully with email: {}", normalizedEmail);
    }
}