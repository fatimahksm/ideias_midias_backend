package com.ideiasmidias.security.config;

import com.ideiasmidias.adminuser.entity.AdminUser;
import com.ideiasmidias.adminuser.repository.AdminUserRepository;
import com.ideiasmidias.common.enums.AdminRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminBootstrapSeeder implements CommandLineRunner {

    private static final Set<String> DISALLOWED_PASSWORD_VALUES = Set.of(
         
            "Admin@123456",
            "admin123456",
            "password",
            "123456"
    );

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.enabled:false}")
    private boolean enabled;

    @Value("${app.bootstrap.admin.full-name:Super Admin}")
    private String fullName;

    @Value("${app.bootstrap.admin.email:}")
    private String email;

    @Value("${app.bootstrap.admin.password:}")
    private String rawPassword;

    @Override
    public void run(String... args) {
        if (!enabled) {
            log.info("Admin bootstrap seeding is disabled.");
            return;
        }

        String normalizedEmail = normalizeEmail(email);
        String normalizedPassword = normalizePassword(rawPassword);
        String normalizedFullName = normalizeFullName(fullName);

        if (normalizedEmail == null || normalizedEmail.isBlank()) {
            log.warn("Bootstrap admin seeding skipped: email is blank.");
            return;
        }

        if (normalizedPassword == null || normalizedPassword.isBlank()) {
            log.warn("Bootstrap admin seeding skipped: password is blank.");
            return;
        }

        if (isDisallowedPassword(normalizedPassword)) {
            log.warn("Bootstrap admin seeding skipped: unsafe default/bootstrap password detected.");
            return;
        }

        if (adminUserRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            log.info("Bootstrap admin already exists: {}", normalizedEmail);
            return;
        }

        AdminUser adminUser = new AdminUser();
        adminUser.setFullName(normalizedFullName);
        adminUser.setEmail(normalizedEmail);
        adminUser.setPasswordHash(passwordEncoder.encode(normalizedPassword));
        adminUser.setRole(AdminRole.SUPER_ADMIN);
        adminUser.setIsActive(true);

        adminUserRepository.save(adminUser);

        log.info("Bootstrap SUPER_ADMIN created successfully with email: {}", normalizedEmail);
    }

    private String normalizeEmail(String value) {
        return value == null ? null : value.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizePassword(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeFullName(String value) {
        if (value == null || value.isBlank()) {
            return "Super Admin";
        }

        return value.trim();
    }

    private boolean isDisallowedPassword(String password) {
        return DISALLOWED_PASSWORD_VALUES.stream()
                .anyMatch(disallowed -> disallowed.equalsIgnoreCase(password));
    }
}