package com.ideiasmidias.section.service;

import com.ideiasmidias.category.repository.SectionCategoryRepository;
import com.ideiasmidias.common.enums.SectionType;
import com.ideiasmidias.common.exception.BadRequestException;
import com.ideiasmidias.common.exception.ConflictException;
import com.ideiasmidias.common.exception.ResourceNotFoundException;
import com.ideiasmidias.contentblock.repository.SectionContentBlockRepository;
import com.ideiasmidias.item.repository.SectionItemRepository;
import com.ideiasmidias.portfolio.repository.PortfolioProjectRepository;
import com.ideiasmidias.section.dto.SectionRequest;
import com.ideiasmidias.section.dto.SectionResponse;
import com.ideiasmidias.section.entity.Section;
import com.ideiasmidias.section.repository.SectionRepository;
import com.ideiasmidias.sectionattribute.repository.SectionAttributeDefinitionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;
    private final SectionContentBlockRepository sectionContentBlockRepository;
    private final SectionCategoryRepository sectionCategoryRepository;
    private final SectionItemRepository sectionItemRepository;
    private final PortfolioProjectRepository portfolioProjectRepository;
    private final SectionAttributeDefinitionRepository sectionAttributeDefinitionRepository;

    @Override
    public SectionResponse create(SectionRequest request) {
        String slug = resolveSlug(request.getSlug(), request.getNameEn(), request.getNamePt(), null);
        validateSectionCompatibilityForType(request.getSectionType(), null);

        Section section = new Section();
        applyRequestToEntity(section, request, slug);

        Section saved = sectionRepository.save(section);
        return mapToResponse(saved);
    }

    @Override
    public SectionResponse update(Long id, SectionRequest request) {
        Section section = getEntityById(id);

        String slug = resolveSlug(request.getSlug(), request.getNameEn(), request.getNamePt(), id);
        validateSectionCompatibilityForType(request.getSectionType(), id);
        applyRequestToEntity(section, request, slug);

        Section saved = sectionRepository.save(section);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public SectionResponse getById(Long id) {
        return mapToResponse(getEntityById(id));
    }

    @Override
    @Transactional
    public SectionResponse getBySlug(String slug) {
        Section section = sectionRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with slug: " + slug));

        return mapToResponse(section);
    }

    @Override
    @Transactional
    public SectionResponse getActiveBySlug(String slug) {
        Section section = sectionRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Active section not found with slug: " + slug));

        return mapToResponse(section);
    }

    @Override
    @Transactional
    public List<SectionResponse> getAll() {
        return sectionRepository.findAllByOrderBySortOrderAscIdAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<SectionResponse> getAllActive() {
        return sectionRepository.findAllByIsActiveTrueOrderBySortOrderAscIdAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<SectionResponse> getByType(SectionType sectionType) {
        return sectionRepository.findAllBySectionTypeOrderBySortOrderAscIdAsc(sectionType)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<SectionResponse> getActiveByType(SectionType sectionType) {
        return sectionRepository.findAllBySectionTypeAndIsActiveTrueOrderBySortOrderAscIdAsc(sectionType)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        Section section = getEntityById(id);

        long contentBlocksCount = sectionContentBlockRepository.countBySection_Id(id);
        long categoriesCount = sectionCategoryRepository.countBySection_Id(id);
        long itemsCount = sectionItemRepository.countBySection_Id(id);
        long portfolioProjectsCount = portfolioProjectRepository.countBySection_Id(id);
        long attributeDefinitionsCount = sectionAttributeDefinitionRepository.countBySection_Id(id);

        if (contentBlocksCount > 0 || categoriesCount > 0 || itemsCount > 0 || portfolioProjectsCount > 0 || attributeDefinitionsCount > 0) {
            throw new BadRequestException(
                    "Cannot delete section while it still has related content. Delete its blocks, categories, items, projects and attribute definitions first."
            );
        }

        sectionRepository.delete(section);
    }

    private Section getEntityById(Long id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + id));
    }

    private void validateSlugUniqueness(String slug, Long currentSectionId) {
        sectionRepository.findBySlug(normalizeSlug(slug)).ifPresent(existing -> {
            if (currentSectionId == null || !existing.getId().equals(currentSectionId)) {
                throw new ConflictException("Section slug already exists: " + slug);
            }
        });
    }

    /**
     * A slug the caller actually typed is validated as-is, so a real conflict
     * surfaces immediately. A blank one is derived from the section's name
     * and given a free "-2", "-3"... suffix on any collision, so neither the
     * admin form nor the Excel import ever has to invent or type one.
     */
    private String resolveSlug(String rawSlug, String nameEn, String namePt, Long currentSectionId) {
        String normalized = normalizeSlug(rawSlug);
        if (normalized != null && !normalized.isEmpty()) {
            validateSlugUniqueness(normalized, currentSectionId);
            return normalized;
        }
        String base = slugify(nameEn != null && !nameEn.isBlank() ? nameEn : namePt);
        return uniqueSlug(base, currentSectionId);
    }

    private String uniqueSlug(String base, Long currentSectionId) {
        String candidate = base;
        int suffix = 2;
        while (isSlugTaken(candidate, currentSectionId)) {
            candidate = base + "-" + suffix++;
        }
        return candidate;
    }

    private boolean isSlugTaken(String slug, Long currentSectionId) {
        return sectionRepository.findBySlug(slug)
                .filter(existing -> currentSectionId == null || !existing.getId().equals(currentSectionId))
                .isPresent();
    }

    private String slugify(String text) {
        if (text == null || text.isBlank()) {
            return "section";
        }
        String withoutAccents = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        String slug = withoutAccents.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+)|(-+$)", "");
        return slug.isEmpty() ? "section" : slug;
    }

    private void applyRequestToEntity(Section section, SectionRequest request, String slug) {
        section.setSlug(slug);
        section.setNamePt(request.getNamePt());
        section.setNameEn(request.getNameEn());
        section.setDescriptionPt(request.getDescriptionPt());
        section.setDescriptionEn(request.getDescriptionEn());
        section.setSectionType(request.getSectionType());
        section.setCoverImageUrl(request.getCoverImageUrl());
        section.setCoverVideoUrl(request.getCoverVideoUrl());
        section.setDisplayVariant(request.getDisplayVariant());
        section.setLayoutStyle(request.getLayoutStyle());
        section.setShowIntro(request.getShowIntro() != null ? request.getShowIntro() : true);
        section.setShowGallery(request.getShowGallery() != null ? request.getShowGallery() : false);
        section.setShowFilters(request.getShowFilters() != null ? request.getShowFilters() : false);
        section.setShowItemDetails(request.getShowItemDetails() != null ? request.getShowItemDetails() : true);
        section.setDetailsViewMode(request.getDetailsViewMode());
        section.setAllowCustomAttributes(request.getAllowCustomAttributes() != null ? request.getAllowCustomAttributes() : true);
        section.setSettingsJson(request.getSettingsJson());
        section.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        section.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
    }

    private void validateSectionCompatibilityForType(SectionType sectionType, Long sectionId) {
        if (sectionType == null || sectionId == null) {
            return;
        }

        long categoriesCount = sectionCategoryRepository.countBySection_Id(sectionId);
        long itemsCount = sectionItemRepository.countBySection_Id(sectionId);
        long portfolioProjectsCount = portfolioProjectRepository.countBySection_Id(sectionId);

        if (sectionType == SectionType.CONTENT && (categoriesCount > 0 || itemsCount > 0 || portfolioProjectsCount > 0)) {
            throw new BadRequestException("CONTENT sections cannot have categories, items or portfolio projects");
        }

        if (sectionType == SectionType.DIRECT_ITEMS && categoriesCount > 0) {
            throw new BadRequestException("DIRECT_ITEMS sections cannot have categories");
        }

        if (sectionType == SectionType.CATEGORY_ITEMS && portfolioProjectsCount > 0) {
            throw new BadRequestException("CATEGORY_ITEMS sections cannot have portfolio projects");
        }

        if (sectionType == SectionType.PORTFOLIO && (categoriesCount > 0 || itemsCount > 0)) {
            throw new BadRequestException("PORTFOLIO sections cannot have categories or section items");
        }
    }

    private String normalizeSlug(String slug) {
        if (slug == null) {
            return null;
        }
        return slug.trim().toLowerCase();
    }

    private SectionResponse mapToResponse(Section section) {
        return SectionResponse.builder()
                .id(section.getId())
                .slug(section.getSlug())
                .namePt(section.getNamePt())
                .nameEn(section.getNameEn())
                .descriptionPt(section.getDescriptionPt())
                .descriptionEn(section.getDescriptionEn())
                .sectionType(section.getSectionType())
                .coverImageUrl(section.getCoverImageUrl())
                .coverVideoUrl(section.getCoverVideoUrl())
                .displayVariant(section.getDisplayVariant())
                .layoutStyle(section.getLayoutStyle())
                .showIntro(section.getShowIntro())
                .showGallery(section.getShowGallery())
                .showFilters(section.getShowFilters())
                .showItemDetails(section.getShowItemDetails())
                .detailsViewMode(section.getDetailsViewMode())
                .allowCustomAttributes(section.getAllowCustomAttributes())
                .settingsJson(section.getSettingsJson())
                .isActive(section.getIsActive())
                .sortOrder(section.getSortOrder())
                .createdAt(section.getCreatedAt())
                .updatedAt(section.getUpdatedAt())
                .build();
    }
}
