package com.ideiasmidias.portfolio.service;

import com.ideiasmidias.common.enums.SectionType;
import com.ideiasmidias.common.exception.BadRequestException;
import com.ideiasmidias.common.exception.ResourceNotFoundException;
import com.ideiasmidias.portfolio.dto.PortfolioProjectRequest;
import com.ideiasmidias.portfolio.dto.PortfolioProjectResponse;
import com.ideiasmidias.portfolio.entity.PortfolioProject;
import com.ideiasmidias.portfolio.repository.PortfolioProjectRepository;
import com.ideiasmidias.section.entity.Section;
import com.ideiasmidias.section.repository.SectionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PortfolioProjectServiceImpl implements PortfolioProjectService {

    private final PortfolioProjectRepository portfolioProjectRepository;
    private final SectionRepository sectionRepository;

    @Override
    public PortfolioProjectResponse create(PortfolioProjectRequest request) {
        Section section = getValidPortfolioSection(request.getSectionId());

        PortfolioProject project = new PortfolioProject();
        applyRequestToEntity(project, request, section);

        PortfolioProject saved = portfolioProjectRepository.save(project);
        return mapToResponse(saved);
    }

    @Override
    public PortfolioProjectResponse update(Long id, PortfolioProjectRequest request) {
        PortfolioProject project = getEntityById(id);
        Section section = getValidPortfolioSection(request.getSectionId());

        applyRequestToEntity(project, request, section);

        PortfolioProject saved = portfolioProjectRepository.save(project);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public PortfolioProjectResponse getById(Long id) {
        return mapToResponse(getEntityById(id));
    }

    @Override
    @Transactional
    public List<PortfolioProjectResponse> getAll() {
        return portfolioProjectRepository.findAllByOrderBySortOrderAscIdAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<PortfolioProjectResponse> getAllActive() {
        return portfolioProjectRepository.findAllByIsActiveTrueOrderBySortOrderAscIdAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<PortfolioProjectResponse> getBySection(Long sectionId) {
        return portfolioProjectRepository.findAllBySection_IdOrderBySortOrderAscIdAsc(sectionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<PortfolioProjectResponse> getActiveBySection(Long sectionId) {
        return portfolioProjectRepository.findAllBySection_IdAndIsActiveTrueOrderBySortOrderAscIdAsc(sectionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<PortfolioProjectResponse> getFeaturedBySection(Long sectionId) {
        return portfolioProjectRepository.findAllBySection_IdAndIsFeaturedTrueOrderBySortOrderAscIdAsc(sectionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<PortfolioProjectResponse> getActiveFeaturedBySection(Long sectionId) {
        return portfolioProjectRepository.findAllBySection_IdAndIsFeaturedTrueAndIsActiveTrueOrderBySortOrderAscIdAsc(sectionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        PortfolioProject project = getEntityById(id);
        portfolioProjectRepository.delete(project);
    }

    private PortfolioProject getEntityById(Long id) {
        return portfolioProjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio project not found with id: " + id));
    }

    private Section getValidPortfolioSection(Long sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));

        if (section.getSectionType() != SectionType.PORTFOLIO) {
            throw new BadRequestException("Portfolio projects can only be added to sections of type PORTFOLIO");
        }

        return section;
    }

    private void applyRequestToEntity(PortfolioProject project, PortfolioProjectRequest request, Section section) {
        project.setSection(section);
        project.setTitlePt(request.getTitlePt());
        project.setTitleEn(request.getTitleEn());
        project.setShortDescriptionPt(request.getShortDescriptionPt());
        project.setShortDescriptionEn(request.getShortDescriptionEn());
        project.setFullDescriptionPt(request.getFullDescriptionPt());
        project.setFullDescriptionEn(request.getFullDescriptionEn());
        project.setClientName(request.getClientName());
        project.setProjectDate(request.getProjectDate());
        project.setLocationPt(request.getLocationPt());
        project.setLocationEn(request.getLocationEn());
        project.setCoverImageUrl(request.getCoverImageUrl());
        project.setVideoUrl(request.getVideoUrl());
        project.setIsFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false);
        project.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        project.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
    }

    private PortfolioProjectResponse mapToResponse(PortfolioProject project) {
        return PortfolioProjectResponse.builder()
                .id(project.getId())
                .sectionId(project.getSection().getId())
                .titlePt(project.getTitlePt())
                .titleEn(project.getTitleEn())
                .shortDescriptionPt(project.getShortDescriptionPt())
                .shortDescriptionEn(project.getShortDescriptionEn())
                .fullDescriptionPt(project.getFullDescriptionPt())
                .fullDescriptionEn(project.getFullDescriptionEn())
                .clientName(project.getClientName())
                .projectDate(project.getProjectDate())
                .locationPt(project.getLocationPt())
                .locationEn(project.getLocationEn())
                .coverImageUrl(project.getCoverImageUrl())
                .videoUrl(project.getVideoUrl())
                .isFeatured(project.getIsFeatured())
                .isActive(project.getIsActive())
                .sortOrder(project.getSortOrder())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}