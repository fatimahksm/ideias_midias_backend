package com.ideiasmidias.adminuser.repository;

import com.ideiasmidias.adminuser.entity.AdminUser;
import com.ideiasmidias.common.enums.AdminRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    Optional<AdminUser> findByEmail(String email);

    Optional<AdminUser> findByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailIgnoreCase(String email);

    long countByRoleAndIsActiveTrue(AdminRole role);
}