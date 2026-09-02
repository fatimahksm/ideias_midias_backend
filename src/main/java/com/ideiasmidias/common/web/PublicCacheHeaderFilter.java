package com.ideiasmidias.common.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Decides the {@code Cache-Control} of every API response.
 *
 * <p>Spring Security stamps {@code no-store} on everything by default, which
 * is right for an admin panel and wrong for the public site: its content
 * changes when the owner edits it, never per visitor, yet every visit was
 * re-querying it. Security's own writer is disabled in {@code SecurityConfig}
 * so this is the single place that decides, and everything that is not a
 * successful public read still gets {@code no-store}.
 *
 * <p>Runs first in the chain so it also covers responses Security produces on
 * its own, such as a 401 before any controller is reached.
 *
 * <p>Uploaded files are left alone — they are served by the resource handler,
 * which caches them far harder because their URLs never change.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PublicCacheHeaderFilter extends OncePerRequestFilter {

    private static final String PUBLIC_PREFIX = "/api/public/";
    private static final String MEDIA_PREFIX = "/uploads/";
    private static final String NO_STORE = "no-cache, no-store, max-age=0, must-revalidate";

    @Value("${app.cache.public-max-age-seconds:60}")
    private long maxAgeSeconds;

    @Value("${app.cache.public-stale-while-revalidate-seconds:300}")
    private long staleWhileRevalidateSeconds;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (request.getRequestURI().startsWith(MEDIA_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Set before the chain runs: a response cannot be changed once its
        // body has been written.
        response.setHeader("Cache-Control", cacheControlFor(request));

        filterChain.doFilter(request, new CacheDowngradingResponse(response));
    }

    private String cacheControlFor(HttpServletRequest request) {
        boolean isPublicRead = "GET".equalsIgnoreCase(request.getMethod())
                && request.getRequestURI().startsWith(PUBLIC_PREFIX);

        if (!isPublicRead || maxAgeSeconds <= 0) {
            return NO_STORE;
        }

        return "public, max-age=" + maxAgeSeconds
                + ", stale-while-revalidate=" + staleWhileRevalidateSeconds;
    }

    /**
     * Takes the cacheable header back off as soon as the response turns out not
     * to be a success, so a 404 for a mistyped slug is not remembered for a
     * minute after the section is created.
     */
    private static final class CacheDowngradingResponse extends HttpServletResponseWrapper {

        private CacheDowngradingResponse(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void setStatus(int status) {
            super.setStatus(status);
            downgradeIfNotSuccessful(status);
        }

        @Override
        public void sendError(int status) throws IOException {
            downgradeIfNotSuccessful(status);
            super.sendError(status);
        }

        @Override
        public void sendError(int status, String message) throws IOException {
            downgradeIfNotSuccessful(status);
            super.sendError(status, message);
        }

        private void downgradeIfNotSuccessful(int status) {
            if (status < 200 || status >= 300) {
                super.setHeader("Cache-Control", NO_STORE);
            }
        }
    }
}
