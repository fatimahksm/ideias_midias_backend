package com.ideiasmidias.audit.service;

import com.ideiasmidias.audit.dto.AdminAuditLogResponse;
import org.springframework.data.domain.Page;

public interface AdminAuditQueryService {

    Page<AdminAuditLogResponse> search(
            String email,
            String action,
            Boolean success,
            int page,
            int size
    );
}