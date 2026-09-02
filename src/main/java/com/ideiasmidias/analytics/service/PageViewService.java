package com.ideiasmidias.analytics.service;

import com.ideiasmidias.analytics.dto.PageViewRequest;
import com.ideiasmidias.analytics.repository.PageViewRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Turns a visit reported by the browser into a row in {@code page_views}.
 *
 * <p>The visit is reported by the browser itself (see the public page-view
 * endpoint) rather than as a side effect of a data request, so server-side
 * rendering, link prefetches and API calls made by the admin panel never
 * inflate the numbers.
 *
 * <p>Two rules keep the dashboard honest:
 * <ul>
 *   <li><b>Who</b> — {@code visitorHash} is SHA-256 of the random id the
 *       browser stores locally, so the same browser stays the same visitor
 *       across reloads and across days. The id is random and never derived
 *       from an IP address, so it cannot be resolved back to a person.
 *       Requests without an id (scripts, very old browsers) fall back to a
 *       day-salted hash of IP + user agent.</li>
 *   <li><b>How often</b> — the same visitor re-opening the same page is
 *       counted once per dedupe window, so leaving and coming back does not
 *       add a view every single time.</li>
 * </ul>
 *
 * <p>Header reading happens here, on the request thread; only plain values are
 * handed to {@link PageViewWriter}, because the servlet request is recycled as
 * soon as the response is written.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PageViewService {

    private static final Pattern VALID_VISITOR_ID = Pattern.compile("^[A-Za-z0-9_-]{8,64}$");

    /** User agents that identify themselves as automated: not real visitors. */
    private static final Pattern BOT_USER_AGENT = Pattern.compile(
            "bot|crawler|crawl|spider|slurp|scrape|headless|monitor|preview|"
                    + "facebookexternalhit|whatsapp|telegram|embedly|lighthouse|pagespeed|"
                    + "curl|wget|python-requests|okhttp|axios|node-fetch|undici|postman"
    );

    private final PageViewRepository pageViewRepository;
    private final PageViewWriter pageViewWriter;

    @Value("${app.analytics.dedupe-window-minutes:30}")
    private long dedupeWindowMinutes;

    public void record(PageViewRequest request, HttpServletRequest httpRequest) {
        try {
            String path = normalizePath(request.getPath());

            if (path == null || isBot(httpRequest)) {
                return;
            }

            String visitorHash = resolveVisitorHash(request.getVisitorId(), httpRequest);

            if (isDuplicate(visitorHash, path)) {
                return;
            }

            pageViewWriter.save(path, normalizeSlug(request.getSectionSlug()), visitorHash);
        } catch (Exception ex) {
            // Tracking must never surface as a user-facing error.
            log.warn("Failed to record page view for path={}: {}", request.getPath(), ex.getMessage());
        }
    }

    /**
     * True when this visitor already opened this page inside the dedupe
     * window. Checked here so a refresh loop never even queues a write.
     */
    private boolean isDuplicate(String visitorHash, String path) {
        if (dedupeWindowMinutes <= 0) {
            return false;
        }

        LocalDateTime windowStart = LocalDateTime.now().minusMinutes(dedupeWindowMinutes);

        return pageViewRepository.existsByVisitorHashAndPathAndViewedAtAfter(visitorHash, path, windowStart);
    }

    private String normalizePath(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }

        String trimmed = path.trim();

        // A query string or fragment still points at the same page, so drop
        // both: "/sections/x" and "/sections/x?utm=..." must not count twice.
        int cut = trimmed.indexOf('?');
        if (cut >= 0) {
            trimmed = trimmed.substring(0, cut);
        }

        cut = trimmed.indexOf('#');
        if (cut >= 0) {
            trimmed = trimmed.substring(0, cut);
        }

        if (!trimmed.startsWith("/")) {
            trimmed = "/" + trimmed;
        }

        if (trimmed.length() > 1 && trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }

        return trimmed.length() > 255 ? trimmed.substring(0, 255) : trimmed;
    }

    private String normalizeSlug(String slug) {
        if (slug == null || slug.isBlank()) {
            return null;
        }

        String trimmed = slug.trim();

        return trimmed.length() > 180 ? trimmed.substring(0, 180) : trimmed;
    }

    private boolean isBot(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");

        if (userAgent == null || userAgent.isBlank()) {
            return true;
        }

        return BOT_USER_AGENT.matcher(userAgent.toLowerCase(Locale.ROOT)).find();
    }

    private String resolveVisitorHash(String visitorId, HttpServletRequest request) {
        String candidate = visitorId == null ? null : visitorId.trim();

        if (candidate != null && VALID_VISITOR_ID.matcher(candidate).matches()) {
            return sha256("visitor|" + candidate);
        }

        String ip = resolveClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        return sha256("fallback|" + ip + "|" + (userAgent == null ? "" : userAgent) + "|" + LocalDate.now());
    }

    private String sha256(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception ex) {
            return "unknown";
        }
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}
