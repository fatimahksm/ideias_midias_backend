package com.ideiasmidias.audit.controller;

import com.ideiasmidias.audit.dto.AdminAuditLogResponse;
import com.ideiasmidias.audit.service.AdminAuditQueryService;
import com.ideiasmidias.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/audit-logs")
@RequiredArgsConstructor
public class AdminAuditLogController {

    private final AdminAuditQueryService auditQueryService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminAuditLogResponse>>> search(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) Boolean success,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<AdminAuditLogResponse> result = auditQueryService.search(
                email,
                action,
                success,
                page,
                size
        );

        return ResponseEntity.ok(
                ApiResponse.<Page<AdminAuditLogResponse>>builder()
                        .success(true)
                        .message("Admin audit logs fetched successfully")
                        .data(result)
                        .build()
        );
    }
}