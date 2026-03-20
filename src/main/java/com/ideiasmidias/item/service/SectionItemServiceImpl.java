package com.ideiasmidias.item.service;

import com.ideiasmidias.category.entity.SectionCategory;
import com.ideiasmidias.category.repository.SectionCategoryRepository;
import com.ideiasmidias.common.enums.SectionType;
import com.ideiasmidias.common.exception.BadRequestException;
import com.ideiasmidias.common.exception.ResourceNotFoundException;
import com.ideiasmidias.item.dto.SectionItemRequest;
import com.ideiasmidias.item.dto.SectionItemResponse;
import com.ideiasmidias.item.entity.SectionItem;
import com.ideiasmidias.item.repository.SectionItemRepository;
import com.ideiasmidias.section.entity.Section;
import com.ideiasmidias.section.repository.SectionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SectionItemServiceImpl implements SectionItemService {

    private final SectionItemRepository sectionItemRepository;
    private final SectionRepository sectionRepository;
    private final SectionCategoryRepository sectionCategoryRepository;

    @Override
    public SectionItemResponse create(SectionItemRequest request) {
        Section section = getValidItemSection(request.getSectionId());
        SectionCategory category = resolveAndValidateCategory(section, request.getCategoryId());

        SectionItem item = new SectionItem();
        applyRequestToEntity(item, request, section, category);

        SectionItem saved = sectionItemRepository.save(item);
        return mapToResponse(saved);
    }

    @Override
    public SectionItemResponse update(Long id, SectionItemRequest request) {
        SectionItem item = getEntityById(id);
        Section section = getValidItemSection(request.getSectionId());
        SectionCategory category = resolveAndValidateCategory(section, request.getCategoryId());

        applyRequestToEntity(item, request, section, category);

        SectionItem saved = sectionItemRepository.save(item);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public SectionItemResponse getById(Long id) {
        return mapToResponse(getEntityById(id));
    }

    @Override
    @Transactional
    public List<SectionItemResponse> getAll() {
        return sectionItemRepository.findAllByOrderBySortOrderAscIdAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<SectionItemResponse> getAllActive() {
        return sectionItemRepository.findAllByIsActiveTrueOrderBySortOrderAscIdAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<SectionItemResponse> getBySection(Long sectionId) {
        return sectionItemRepository.findAllBySection_IdOrderBySortOrderAscIdAsc(sectionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<SectionItemResponse> getActiveBySection(Long sectionId) {
        return sectionItemRepository.findAllBySection_IdAndIsActiveTrueOrderBySortOrderAscIdAsc(sectionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<SectionItemResponse> getByCategory(Long categoryId) {
        return sectionItemRepository.findAllByCategory_IdOrderBySortOrderAscIdAsc(categoryId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<SectionItemResponse> getActiveByCategory(Long categoryId) {
        return sectionItemRepository.findAllByCategory_IdAndIsActiveTrueOrderBySortOrderAscIdAsc(categoryId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<SectionItemResponse> getDirectItemsBySection(Long sectionId) {
        return sectionItemRepository.findAllBySection_IdAndCategoryIsNullOrderBySortOrderAscIdAsc(sectionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<SectionItemResponse> getActiveDirectItemsBySection(Long sectionId) {
        return sectionItemRepository.findAllBySection_IdAndCategoryIsNullAndIsActiveTrueOrderBySortOrderAscIdAsc(sectionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<SectionItemResponse> getFeaturedBySection(Long sectionId) {
        return sectionItemRepository.findAllBySection_IdAndIsFeaturedTrueOrderBySortOrderAscIdAsc(sectionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<SectionItemResponse> getActiveFeaturedBySection(Long sectionId) {
        return sectionItemRepository.findAllBySection_IdAndIsFeaturedTrueAndIsActiveTrueOrderBySortOrderAscIdAsc(sectionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        SectionItem item = getEntityById(id);
        sectionItemRepository.delete(item);
    }

    private SectionItem getEntityById(Long id) {
        return sectionItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section item not found with id: " + id));
    }

    private Section getValidItemSection(Long sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));

        if (section.getSectionType() != SectionType.CATEGORY_ITEMS
                && section.getSectionType() != SectionType.DIRECT_ITEMS) {
            throw new BadRequestException("Items can only be added to sections of type CATEGORY_ITEMS or DIRECT_ITEMS");
        }

        return section;
    }

    private SectionCategory resolveAndValidateCategory(Section section, Long categoryId) {
        if (section.getSectionType() == SectionType.DIRECT_ITEMS) {
            if (categoryId != null) {
                throw new BadRequestException("DIRECT_ITEMS sections cannot have category-based items");
            }
            return null;
        }

        if (section.getSectionType() == SectionType.CATEGORY_ITEMS) {
            if (categoryId == null) {
                throw new BadRequestException("CATEGORY_ITEMS sections require a category");
            }

            SectionCategory category = sectionCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Section category not found with id: " + categoryId));

            if (!category.getSection().getId().equals(section.getId())) {
                throw new BadRequestException("The selected category does not belong to the selected section");
            }

            return category;
        }

        return null;
    }

    private void applyRequestToEntity(SectionItem item, SectionItemRequest request, Section section, SectionCategory category) {
        item.setSection(section);
        item.setCategory(category);
        item.setTitlePt(request.getTitlePt());
        item.setTitleEn(request.getTitleEn());
        item.setShortDescriptionPt(request.getShortDescriptionPt());
        item.setShortDescriptionEn(request.getShortDescriptionEn());
        item.setFullDescriptionPt(request.getFullDescriptionPt());
        item.setFullDescriptionEn(request.getFullDescriptionEn());
        item.setCoverImageUrl(request.getCoverImageUrl());
        item.setVideoUrl(request.getVideoUrl());
        item.setItemType(request.getItemType());
        item.setSpecificationsPt(request.getSpecificationsPt());
        item.setSpecificationsEn(request.getSpecificationsEn());
        item.setIsFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false);
        item.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        item.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
    }

    private SectionItemResponse mapToResponse(SectionItem item) {
        return SectionItemResponse.builder()
                .id(item.getId())
                .sectionId(item.getSection().getId())
                .categoryId(item.getCategory() != null ? item.getCategory().getId() : null)
                .titlePt(item.getTitlePt())
                .titleEn(item.getTitleEn())
                .shortDescriptionPt(item.getShortDescriptionPt())
                .shortDescriptionEn(item.getShortDescriptionEn())
                .fullDescriptionPt(item.getFullDescriptionPt())
                .fullDescriptionEn(item.getFullDescriptionEn())
                .coverImageUrl(item.getCoverImageUrl())
                .videoUrl(item.getVideoUrl())
                .itemType(item.getItemType())
                .specificationsPt(item.getSpecificationsPt())
                .specificationsEn(item.getSpecificationsEn())
                .isFeatured(item.getIsFeatured())
                .isActive(item.getIsActive())
                .sortOrder(item.getSortOrder())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}