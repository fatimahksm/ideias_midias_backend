package com.ideiasmidias.analytics.service;

import com.ideiasmidias.analytics.entity.PageView;
import com.ideiasmidias.analytics.repository.PageViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Writes the row off the request thread. Recording a view is a single cheap
 * insert, but it must never slow down or fail the request it rides along with.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PageViewWriter {

    private final PageViewRepository pageViewRepository;

    @Async("pageViewExecutor")
    public void save(String path, String sectionSlug, String visitorHash) {
        try {
            PageView view = new PageView();
            view.setPath(path);
            view.setSectionSlug(sectionSlug);
            view.setVisitorHash(visitorHash);
            view.setViewedAt(LocalDateTime.now());

            pageViewRepository.save(view);
        } catch (Exception ex) {
            log.warn("Failed to persist page view for path={}: {}", path, ex.getMessage());
        }
    }
}
