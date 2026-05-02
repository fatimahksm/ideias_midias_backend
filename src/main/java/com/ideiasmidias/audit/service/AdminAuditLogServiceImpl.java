package com.ideiasmidias.audit.service;

import com.ideiasmidias.audit.entity.AdminAuditLog;
import com.ideiasmidias.audit.repository.AdminAuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAuditLogServiceImpl implements AdminAuditLogService {

    private final AdminAuditLogRepository repository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(
            Long adminId,
            String email,
            String action,
            boolean success,
            String ipAddress,
            String userAgent,
            String message
    ) {
        try {
            repository.save(
                    AdminAuditLog.builder()
                            .adminId(adminId)
                            .email(email)
                            .action(action)
                            .success(success)
                            .ipAddress(ipAddress)
                            .userAgent(userAgent)
                            .message(message)
                            .build()
            );
        } catch (Exception ex) {
            log.warn("Failed to write admin audit log. action={}, email={}", action, email, ex);
        }
    }
}