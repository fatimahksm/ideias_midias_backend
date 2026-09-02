package com.ideiasmidias.analytics.service;

import com.ideiasmidias.analytics.entity.PageView;
import com.ideiasmidias.analytics.repository.PageViewRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Records a visit for every public page load. No cookies, no persistent
 * identifiers: {@code visitorHash} is SHA-256(ip + userAgent + today's
 * date), so the same visitor hashes the same way for "unique visitors
 * today/this month" without ever being linkable across days or resolvable
 * back to an IP.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PageViewService {

    private final PageViewRepository pageViewRepository;

    @Async("pageViewExecutor")
    public void record(String path, String sectionSlug, HttpServletRequest request) {
        try {
            PageView view = new PageView();
            view.setPath(path);
            view.setSectionSlug(sectionSlug);
            view.setVisitorHash(hashVisitor(request));
            view.setViewedAt(LocalDateTime.now());

            pageViewRepository.save(view);
        } catch (Exception ex) {
            // Tracking must never surface as a user-facing error.
            log.warn("Failed to record page view for path={}: {}", path, ex.getMessage());
        }
    }

    private String hashVisitor(HttpServletRequest request) {
        String ip = resolveClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        String raw = ip + "|" + (userAgent == null ? "" : userAgent) + "|" + LocalDate.now();

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
