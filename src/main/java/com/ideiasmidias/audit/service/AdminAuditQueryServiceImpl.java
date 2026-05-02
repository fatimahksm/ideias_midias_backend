package com.ideiasmidias.audit.service;

import com.ideiasmidias.audit.dto.AdminAuditLogResponse;
import com.ideiasmidias.audit.repository.AdminAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AdminAuditQueryServiceImpl implements AdminAuditQueryService {

    private final AdminAuditLogRepository repository;

    @Override
    public Page<AdminAuditLogResponse> search(
            String email,
            String action,
            Boolean success,
            int page,
            int size
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        Pageable pageable = PageRequest.of(safePage, safeSize);

        String cleanEmail = StringUtils.hasText(email) ? email.trim() : null;
        String cleanAction = StringUtils.hasText(action) ? action.trim() : null;

        return repository
                .searchAuditLogs(cleanEmail, cleanAction, success, pageable)
                .map(AdminAuditLogResponse::fromEntity);
    }
}