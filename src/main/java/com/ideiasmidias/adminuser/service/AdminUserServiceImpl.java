package com.ideiasmidias.adminuser.service;

import com.ideiasmidias.adminuser.dto.AdminUserResponse;
import com.ideiasmidias.adminuser.dto.CreateAdminUserRequest;
import com.ideiasmidias.adminuser.dto.ResetAdminPasswordRequest;
import com.ideiasmidias.adminuser.dto.UpdateAdminUserRequest;
import com.ideiasmidias.adminuser.dto.UpdateAdminUserStatusRequest;
import com.ideiasmidias.adminuser.entity.AdminUser;
import com.ideiasmidias.adminuser.repository.AdminUserRepository;
import com.ideiasmidias.common.enums.AdminRole;
import com.ideiasmidias.common.exception.BadRequestException;
import com.ideiasmidias.common.exception.ConflictException;
import com.ideiasmidias.common.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserServiceImpl implements AdminUserService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AdminUserResponse createAdmin(CreateAdminUserRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());

        if (adminUserRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ConflictException("Admin user email already exists: " + normalizedEmail);
        }

        AdminUser adminUser = new AdminUser();
        adminUser.setFullName(request.getFullName().trim());
        adminUser.setEmail(normalizedEmail);
        adminUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        adminUser.setRole(AdminRole.ADMIN);
        adminUser.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        AdminUser saved = adminUserRepository.save(adminUser);

        return mapToResponse(saved);
    }

    @Override
    public List<AdminUserResponse> getAllAdmins() {
        return adminUserRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AdminUserResponse getAdminById(Long id) {
        AdminUser adminUser = adminUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found with id: " + id));

        return mapToResponse(adminUser);
    }

    @Override
    public AdminUserResponse updateAdmin(Long id, UpdateAdminUserRequest request, Long currentAdminId) {
        AdminUser adminUser = adminUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found with id: " + id));

        String normalizedEmail = normalizeEmail(request.getEmail());

        AdminUser existingByEmail = adminUserRepository.findByEmailIgnoreCase(normalizedEmail).orElse(null);
        if (existingByEmail != null && !Objects.equals(existingByEmail.getId(), adminUser.getId())) {
            throw new ConflictException("Admin user email already exists: " + normalizedEmail);
        }

        if (Objects.equals(currentAdminId, adminUser.getId())
                && adminUser.getRole() != request.getRole()) {
            throw new BadRequestException("You cannot change your own role");
        }

        if (adminUser.getRole() == AdminRole.SUPER_ADMIN
                && request.getRole() != AdminRole.SUPER_ADMIN) {

            long activeSuperAdmins = adminUserRepository.countByRoleAndIsActiveTrue(AdminRole.SUPER_ADMIN);

            if (Boolean.TRUE.equals(adminUser.getIsActive()) && activeSuperAdmins <= 1) {
                throw new BadRequestException("You cannot demote the last active SUPER_ADMIN");
            }
        }

        adminUser.setFullName(request.getFullName().trim());
        adminUser.setEmail(normalizedEmail);
        adminUser.setRole(request.getRole());

        AdminUser saved = adminUserRepository.save(adminUser);

        return mapToResponse(saved);
    }

    @Override
    public AdminUserResponse updateAdminStatus(Long id, UpdateAdminUserStatusRequest request, Long currentAdminId) {
        AdminUser adminUser = adminUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found with id: " + id));

        Boolean newIsActive = request.getIsActive();

        if (Objects.equals(currentAdminId, adminUser.getId()) && Boolean.FALSE.equals(newIsActive)) {
            throw new BadRequestException("You cannot deactivate your own account");
        }

        if (adminUser.getRole() == AdminRole.SUPER_ADMIN
                && Boolean.TRUE.equals(adminUser.getIsActive())
                && Boolean.FALSE.equals(newIsActive)) {

            long activeSuperAdmins = adminUserRepository.countByRoleAndIsActiveTrue(AdminRole.SUPER_ADMIN);

            if (activeSuperAdmins <= 1) {
                throw new BadRequestException("You cannot deactivate the last active SUPER_ADMIN");
            }
        }

        adminUser.setIsActive(newIsActive);

        AdminUser saved = adminUserRepository.save(adminUser);

        return mapToResponse(saved);
    }

    @Override
    public void resetAdminPassword(Long id, ResetAdminPasswordRequest request, Long currentAdminId) {
        AdminUser adminUser = adminUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found with id: " + id));

        if (Objects.equals(currentAdminId, adminUser.getId())) {
            throw new BadRequestException("You cannot reset your own password through this endpoint");
        }

        if (!Objects.equals(request.getNewPassword(), request.getConfirmPassword())) {
            throw new BadRequestException("New password and confirm password do not match");
        }

        adminUser.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        adminUserRepository.save(adminUser);
    }

    @Override
    public void deleteAdmin(Long id, Long currentAdminId) {
        AdminUser adminUser = adminUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found with id: " + id));

        if (Objects.equals(currentAdminId, adminUser.getId())) {
            throw new BadRequestException("You cannot delete your own account");
        }

        if (adminUser.getRole() == AdminRole.SUPER_ADMIN && Boolean.TRUE.equals(adminUser.getIsActive())) {
            long activeSuperAdmins = adminUserRepository.countByRoleAndIsActiveTrue(AdminRole.SUPER_ADMIN);

            if (activeSuperAdmins <= 1) {
                throw new BadRequestException("You cannot delete the last active SUPER_ADMIN");
            }
        }

        adminUserRepository.delete(adminUser);
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private AdminUserResponse mapToResponse(AdminUser adminUser) {
        return AdminUserResponse.builder()
                .id(adminUser.getId())
                .fullName(adminUser.getFullName())
                .email(adminUser.getEmail())
                .role(adminUser.getRole().name())
                .isActive(adminUser.getIsActive())
                .lastLoginAt(adminUser.getLastLoginAt())
                .createdAt(adminUser.getCreatedAt())
                .updatedAt(adminUser.getUpdatedAt())
                .build();
    }
}