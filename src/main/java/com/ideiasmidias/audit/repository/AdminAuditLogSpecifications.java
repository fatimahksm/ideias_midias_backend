package com.ideiasmidias.audit.repository;

import com.ideiasmidias.audit.entity.AdminAuditLog;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

/**
 * Filter predicates for the admin audit log search. A filter that was not
 * supplied contributes {@link Specification#unrestricted()} instead of a
 * predicate, so an unfiltered search really is an unfiltered query and no null
 * is ever bound as a parameter.
 */
public final class AdminAuditLogSpecifications {

    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_ACTION = "action";
    private static final String FIELD_SUCCESS = "success";

    private AdminAuditLogSpecifications() {
    }

    /** Case-insensitive "contains" on the admin's email. */
    public static Specification<AdminAuditLog> emailContains(String email) {
        if (email == null || email.isBlank()) {
            return Specification.unrestricted();
        }

        String pattern = "%" + email.trim().toLowerCase(Locale.ROOT) + "%";

        return (root, query, builder) ->
                builder.like(builder.lower(root.get(FIELD_EMAIL)), pattern);
    }

    /** Exact match on the action name, e.g. {@code ADMIN_LOGIN_FAILED}. */
    public static Specification<AdminAuditLog> hasAction(String action) {
        if (action == null || action.isBlank()) {
            return Specification.unrestricted();
        }

        return (root, query, builder) ->
                builder.equal(root.get(FIELD_ACTION), action.trim());
    }

    public static Specification<AdminAuditLog> hasSuccess(Boolean success) {
        if (success == null) {
            return Specification.unrestricted();
        }

        return (root, query, builder) ->
                builder.equal(root.get(FIELD_SUCCESS), success);
    }
}
