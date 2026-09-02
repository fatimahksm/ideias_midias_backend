package com.ideiasmidias.common.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Page parameters arrive from the browser, so they are clamped here: a
 * negative page or an oversized page size must never reach the database.
 */
public final class PageRequestFactory {

    public static final int DEFAULT_PAGE_SIZE = 24;
    public static final int MAX_PAGE_SIZE = 100;

    private PageRequestFactory() {
    }

    public static Pageable of(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);

        return PageRequest.of(safePage, safeSize);
    }
}
