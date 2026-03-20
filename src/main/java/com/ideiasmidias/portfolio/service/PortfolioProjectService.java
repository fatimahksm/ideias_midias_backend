package com.ideiasmidias.portfolio.service;

import com.ideiasmidias.portfolio.dto.PortfolioProjectRequest;
import com.ideiasmidias.portfolio.dto.PortfolioProjectResponse;

import java.util.List;

public interface PortfolioProjectService {

    PortfolioProjectResponse create(PortfolioProjectRequest request);

    PortfolioProjectResponse update(Long id, PortfolioProjectRequest request);

    PortfolioProjectResponse getById(Long id);

    List<PortfolioProjectResponse> getAll();

    List<PortfolioProjectResponse> getAllActive();

    List<PortfolioProjectResponse> getBySection(Long sectionId);

    List<PortfolioProjectResponse> getActiveBySection(Long sectionId);

    List<PortfolioProjectResponse> getFeaturedBySection(Long sectionId);

    List<PortfolioProjectResponse> getActiveFeaturedBySection(Long sectionId);

    void delete(Long id);
}