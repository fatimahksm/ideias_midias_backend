package com.ideiasmidias.category.service;

import com.ideiasmidias.category.dto.SectionCategoryRequest;
import com.ideiasmidias.category.dto.SectionCategoryResponse;
import com.ideiasmidias.category.entity.SectionCategory;
import com.ideiasmidias.category.repository.SectionCategoryRepository;
import com.ideiasmidias.common.enums.SectionType;
import com.ideiasmidias.common.exception.BadRequestException;
import com.ideiasmidias.common.exception.ResourceNotFoundException;
import com.ideiasmidias.section.entity.Section;
import com.ideiasmidias.section.repository.SectionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SectionCategoryServiceImpl implements SectionCategoryService {

    private final SectionCategoryRepository sectionCategoryRepository;
    private final SectionRepository sectionRepository;

    @Override
    public SectionCategoryResponse create(SectionCategoryRequest request) {
        Section section = getValidCategorySection(request.getSectionId());

        SectionCategory category = new SectionCategory();
        applyRequestToEntity(category, request, section);

        SectionCategory saved = sectionCategoryRepository.save(category);
        return mapToResponse(saved);
    }

    @Override
    public SectionCategoryResponse update(Long id, SectionCategoryRequest request) {
        SectionCategory category = getEntityById(id);
        Section section = getValidCategorySection(request.getSectionId());

        applyRequestToEntity(category, request, section);

        SectionCategory saved = sectionCategoryRepository.save(category);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public SectionCategoryResponse getById(Long id) {
        return mapToResponse(getEntityById(id));
    }

    @Override
    @Transactional
    public List<SectionCategoryResponse> getAll() {
        return sectionCategoryRepository.findAllByOrderBySortOrderAscIdAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<SectionCategoryResponse> getAllActive() {
        return sectionCategoryRepository.findAllByIsActiveTrueOrderBySortOrderAscIdAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<SectionCategoryResponse> getBySection(Long sectionId) {
        return sectionCategoryRepository.findAllBySection_IdOrderBySortOrderAscIdAsc(sectionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<SectionCategoryResponse> getActiveBySection(Long sectionId) {
        return sectionCategoryRepository.findAllBySection_IdAndIsActiveTrueOrderBySortOrderAscIdAsc(sectionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        SectionCategory category = getEntityById(id);
        sectionCategoryRepository.delete(category);
    }

    private SectionCategory getEntityById(Long id) {
        return sectionCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section category not found with id: " + id));
    }

    private Section getValidCategorySection(Long sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));

        if (section.getSectionType() != SectionType.CATEGORY_ITEMS) {
            throw new BadRequestException("Categories can only be added to sections of type CATEGORY_ITEMS");
        }

        return section;
    }

    private void applyRequestToEntity(SectionCategory category, SectionCategoryRequest request, Section section) {
        category.setSection(section);
        category.setNamePt(request.getNamePt());
        category.setNameEn(request.getNameEn());
        category.setDescriptionPt(request.getDescriptionPt());
        category.setDescriptionEn(request.getDescriptionEn());
        category.setImageUrl(request.getImageUrl());
        category.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
    }

    private SectionCategoryResponse mapToResponse(SectionCategory category) {
        return SectionCategoryResponse.builder()
                .id(category.getId())
                .sectionId(category.getSection().getId())
                .namePt(category.getNamePt())
                .nameEn(category.getNameEn())
                .descriptionPt(category.getDescriptionPt())
                .descriptionEn(category.getDescriptionEn())
                .imageUrl(category.getImageUrl())
                .isActive(category.getIsActive())
                .sortOrder(category.getSortOrder())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}