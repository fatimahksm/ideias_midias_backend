package com.ideiasmidias.audit.service;

import com.ideiasmidias.audit.dto.AdminAuditLogResponse;
import com.ideiasmidias.audit.entity.AdminAuditLog;
import com.ideiasmidias.audit.repository.AdminAuditLogRepository;
import com.ideiasmidias.audit.repository.AdminAuditLogSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAuditQueryServiceImpl implements AdminAuditQueryService {

    private static final int MIN_PAGE_SIZE = 1;
    private static final int MAX_PAGE_SIZE = 100;

    /** Newest first: the audit log is read to answer "what just happened?". */
    private static final Sort NEWEST_FIRST = Sort.by(Sort.Direction.DESC, "createdAt");

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
        int safeSize = Math.min(Math.max(size, MIN_PAGE_SIZE), MAX_PAGE_SIZE);

        Pageable pageable = PageRequest.of(safePage, safeSize, NEWEST_FIRST);

        Specification<AdminAuditLog> specification = Specification
                .allOf(
                        AdminAuditLogSpecifications.emailContains(email),
                        AdminAuditLogSpecifications.hasAction(action),
                        AdminAuditLogSpecifications.hasSuccess(success)
                );

        return repository
                .findAll(specification, pageable)
                .map(AdminAuditLogResponse::fromEntity);
    }
}
