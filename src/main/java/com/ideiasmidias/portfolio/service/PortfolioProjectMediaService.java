package com.ideiasmidias.portfolio.service;

import com.ideiasmidias.common.enums.MediaType;
import com.ideiasmidias.portfolio.dto.PortfolioProjectMediaRequest;
import com.ideiasmidias.portfolio.dto.PortfolioProjectMediaResponse;

import java.util.List;

public interface PortfolioProjectMediaService {

    PortfolioProjectMediaResponse create(PortfolioProjectMediaRequest request);

    PortfolioProjectMediaResponse update(Long id, PortfolioProjectMediaRequest request);

    PortfolioProjectMediaResponse getById(Long id);

    List<PortfolioProjectMediaResponse> getAll();

    List<PortfolioProjectMediaResponse> getAllActive();

    List<PortfolioProjectMediaResponse> getByProject(Long projectId);

    List<PortfolioProjectMediaResponse> getActiveByProject(Long projectId);

    List<PortfolioProjectMediaResponse> getByProjectAndType(Long projectId, MediaType mediaType);

    List<PortfolioProjectMediaResponse> getActiveByProjectAndType(Long projectId, MediaType mediaType);

    void delete(Long id);
}