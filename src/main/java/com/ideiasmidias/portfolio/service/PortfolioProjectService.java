package com.ideiasmidias.portfolio.service;

import com.ideiasmidias.portfolio.dto.PortfolioProjectRequest;
import com.ideiasmidias.common.response.PageResponse;
import com.ideiasmidias.portfolio.dto.PortfolioProjectResponse;
import com.ideiasmidias.portfolio.dto.PortfolioProjectStatsResponse;

import java.util.List;

public interface PortfolioProjectService {

    PortfolioProjectResponse create(PortfolioProjectRequest request);

    PortfolioProjectResponse update(Long id, PortfolioProjectRequest request);

    PortfolioProjectResponse getById(Long id);

    List<PortfolioProjectResponse> getAll();

    /** Counts for the stat cards, scoped the same way the listing is. */
    PortfolioProjectStatsResponse stats(Long sectionId);

    /** One filtered, sorted page of projects. Every filter is optional. */
    PageResponse<PortfolioProjectResponse> search(
            Long sectionId,
            String status,
            Boolean isFeatured,
            String search,
            String sort,
            int page,
            int size
    );

    List<PortfolioProjectResponse> getAllActive();

    List<PortfolioProjectResponse> getBySection(Long sectionId);

    List<PortfolioProjectResponse> getActiveBySection(Long sectionId);

    List<PortfolioProjectResponse> getFeaturedBySection(Long sectionId);

    List<PortfolioProjectResponse> getActiveFeaturedBySection(Long sectionId);

    void delete(Long id);
}