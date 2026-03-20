package com.ideiasmidias.portfolio.service;

import com.ideiasmidias.common.enums.MediaType;
import com.ideiasmidias.common.exception.ResourceNotFoundException;
import com.ideiasmidias.portfolio.dto.PortfolioProjectMediaRequest;
import com.ideiasmidias.portfolio.dto.PortfolioProjectMediaResponse;
import com.ideiasmidias.portfolio.entity.PortfolioProject;
import com.ideiasmidias.portfolio.entity.PortfolioProjectMedia;
import com.ideiasmidias.portfolio.repository.PortfolioProjectMediaRepository;
import com.ideiasmidias.portfolio.repository.PortfolioProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PortfolioProjectMediaServiceImpl implements PortfolioProjectMediaService {

    private final PortfolioProjectMediaRepository portfolioProjectMediaRepository;
    private final PortfolioProjectRepository portfolioProjectRepository;

    @Override
    public PortfolioProjectMediaResponse create(PortfolioProjectMediaRequest request) {
        PortfolioProject project = getProjectById(request.getProjectId());

        PortfolioProjectMedia media = new PortfolioProjectMedia();
        applyRequestToEntity(media, request, project);

        PortfolioProjectMedia saved = portfolioProjectMediaRepository.save(media);
        return mapToResponse(saved);
    }

    @Override
    public PortfolioProjectMediaResponse update(Long id, PortfolioProjectMediaRequest request) {
        PortfolioProjectMedia media = getEntityById(id);
        PortfolioProject project = getProjectById(request.getProjectId());

        applyRequestToEntity(media, request, project);

        PortfolioProjectMedia saved = portfolioProjectMediaRepository.save(media);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public PortfolioProjectMediaResponse getById(Long id) {
        return mapToResponse(getEntityById(id));
    }

    @Override
    @Transactional
    public List<PortfolioProjectMediaResponse> getAll() {
        return portfolioProjectMediaRepository.findAllByOrderBySortOrderAscIdAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<PortfolioProjectMediaResponse> getAllActive() {
        return portfolioProjectMediaRepository.findAllByIsActiveTrueOrderBySortOrderAscIdAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<PortfolioProjectMediaResponse> getByProject(Long projectId) {
        return portfolioProjectMediaRepository.findAllByProject_IdOrderBySortOrderAscIdAsc(projectId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<PortfolioProjectMediaResponse> getActiveByProject(Long projectId) {
        return portfolioProjectMediaRepository.findAllByProject_IdAndIsActiveTrueOrderBySortOrderAscIdAsc(projectId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<PortfolioProjectMediaResponse> getByProjectAndType(Long projectId, MediaType mediaType) {
        return portfolioProjectMediaRepository.findAllByProject_IdAndMediaTypeOrderBySortOrderAscIdAsc(projectId, mediaType)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<PortfolioProjectMediaResponse> getActiveByProjectAndType(Long projectId, MediaType mediaType) {
        return portfolioProjectMediaRepository.findAllByProject_IdAndMediaTypeAndIsActiveTrueOrderBySortOrderAscIdAsc(projectId, mediaType)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        PortfolioProjectMedia media = getEntityById(id);
        portfolioProjectMediaRepository.delete(media);
    }

    private PortfolioProjectMedia getEntityById(Long id) {
        return portfolioProjectMediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio project media not found with id: " + id));
    }

    private PortfolioProject getProjectById(Long id) {
        return portfolioProjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio project not found with id: " + id));
    }

    private void applyRequestToEntity(PortfolioProjectMedia media, PortfolioProjectMediaRequest request, PortfolioProject project) {
        media.setProject(project);
        media.setMediaType(request.getMediaType());
        media.setMediaUrl(request.getMediaUrl());
        media.setThumbnailUrl(request.getThumbnailUrl());
        media.setAltTextPt(request.getAltTextPt());
        media.setAltTextEn(request.getAltTextEn());
        media.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        media.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
    }

    private PortfolioProjectMediaResponse mapToResponse(PortfolioProjectMedia media) {
        return PortfolioProjectMediaResponse.builder()
                .id(media.getId())
                .projectId(media.getProject().getId())
                .mediaType(media.getMediaType())
                .mediaUrl(media.getMediaUrl())
                .thumbnailUrl(media.getThumbnailUrl())
                .altTextPt(media.getAltTextPt())
                .altTextEn(media.getAltTextEn())
                .isActive(media.getIsActive())
                .sortOrder(media.getSortOrder())
                .createdAt(media.getCreatedAt())
                .updatedAt(media.getUpdatedAt())
                .build();
    }
}