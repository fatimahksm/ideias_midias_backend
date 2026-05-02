package com.ideiasmidias.audit.dto;

import com.ideiasmidias.audit.entity.AdminAuditLog;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminAuditLogResponse {

    private Long id;
    private Long adminId;
    private String email;
    private String action;
    private Boolean success;
    private String ipAddress;
    private String userAgent;
    private String message;
    private LocalDateTime createdAt;

    public static AdminAuditLogResponse fromEntity(AdminAuditLog log) {
        return AdminAuditLogResponse.builder()
                .id(log.getId())
                .adminId(log.getAdminId())
                .email(log.getEmail())
                .action(log.getAction())
                .success(log.getSuccess())
                .ipAddress(log.getIpAddress())
                .userAgent(log.getUserAgent())
                .message(log.getMessage())
                .createdAt(log.getCreatedAt())
                .build();
    }
}