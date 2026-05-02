package com.ideiasmidias.audit.service;

public interface AdminAuditLogService {

    void log(
            Long adminId,
            String email,
            String action,
            boolean success,
            String ipAddress,
            String userAgent,
            String message
    );
}