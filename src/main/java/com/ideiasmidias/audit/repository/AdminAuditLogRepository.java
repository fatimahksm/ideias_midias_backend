package com.ideiasmidias.audit.repository;

import com.ideiasmidias.audit.entity.AdminAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdminAuditLogRepository extends JpaRepository<AdminAuditLog, Long> {

    @Query("""
            SELECT log
            FROM AdminAuditLog log
            WHERE (:email IS NULL OR LOWER(log.email) LIKE LOWER(CONCAT('%', :email, '%')))
              AND (:action IS NULL OR log.action = :action)
              AND (:success IS NULL OR log.success = :success)
            ORDER BY log.createdAt DESC
            """)
    Page<AdminAuditLog> searchAuditLogs(
            @Param("email") String email,
            @Param("action") String action,
            @Param("success") Boolean success,
            Pageable pageable
    );
}