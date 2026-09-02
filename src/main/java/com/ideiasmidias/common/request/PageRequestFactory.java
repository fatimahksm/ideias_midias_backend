package com.ideiasmidias.common.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
        return PageRequest.of(safePage(page), safeSize(size));
    }

    public static Pageable of(int page, int size, Sort sort) {
        return PageRequest.of(safePage(page), safeSize(size), sort);
    }

    private static int safePage(int page) {
        return Math.max(page, 0);
    }

    private static int safeSize(int size) {
        return size <= 0 ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);
    }
}
