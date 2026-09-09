package com.ideiasmidias.audit.repository;

import com.ideiasmidias.audit.entity.AdminAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Filtering happens through {@link AdminAuditLogSpecifications} rather than a
 * {@code @Query} with {@code :param IS NULL OR ...} clauses. Postgres has no
 * type to infer for a null bind parameter and falls back to {@code bytea},
 * which made every unfiltered call fail outright. A Specification simply omits
 * the predicate when a filter is absent, so no null is ever bound.
 */
public interface AdminAuditLogRepository
        extends JpaRepository<AdminAuditLog, Long>, JpaSpecificationExecutor<AdminAuditLog> {
}
