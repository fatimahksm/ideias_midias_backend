package com.ideiasmidias.common.request;

import org.springframework.data.domain.Sort;

/**
 * The shape every browsable admin list shares: a free-text search, a status,
 * and a sort choice. Kept in one place so the screens stay consistent and the
 * values coming from the browser are normalised once.
 */
public final class ListQuery {

    private ListQuery() {
    }

    /**
     * A ready LIKE pattern — lower-cased, trimmed and wrapped in wildcards —
     * or null when the search box is empty. Built here rather than in the
     * query so the parameter has a type Postgres can work with.
     */
    public static String searchPattern(String raw) {
        if (raw == null) return null;

        String trimmed = raw.trim().toLowerCase();

        return trimmed.isEmpty() ? null : "%" + trimmed + "%";
    }

    /**
     * "ACTIVE" / "INACTIVE" become true / false; anything else (including
     * "ALL") means no filter.
     */
    public static Boolean status(String raw) {
        if ("ACTIVE".equalsIgnoreCase(raw)) return Boolean.TRUE;
        if ("INACTIVE".equalsIgnoreCase(raw)) return Boolean.FALSE;

        return null;
    }

    /**
     * Sorting is chosen from a fixed set rather than passed through, so a
     * request can never sort by an arbitrary column.
     */
    public static Sort sort(String raw, String titleField) {
        if ("updatedAt".equalsIgnoreCase(raw)) {
            return Sort.by(Sort.Direction.DESC, "updatedAt");
        }

        if ("title".equalsIgnoreCase(raw)) {
            return Sort.by(Sort.Direction.ASC, titleField);
        }

        return Sort.by(Sort.Order.asc("sortOrder"), Sort.Order.asc("id"));
    }
}
