package com.ideiasmidias.section.service;

import com.ideiasmidias.common.enums.SectionType;
import com.ideiasmidias.common.exception.ConflictException;
import com.ideiasmidias.common.exception.ResourceNotFoundException;
import com.ideiasmidias.section.dto.SectionRequest;
import com.ideiasmidias.section.dto.SectionResponse;
import com.ideiasmidias.section.entity.Section;
import com.ideiasmidias.section.repository.SectionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;

    @Override
    public SectionResponse create(SectionRequest request) {
        validateSlugUniqueness(request.getSlug(), null);

        Section section = new Section();
        applyRequestToEntity(section, request);

        Section saved = sectionRepository.save(section);
        return mapToResponse(saved);
    }

    @Override
    public SectionResponse update(Long id, SectionRequest request) {
        Section section = getEntityById(id);

        validateSlugUniqueness(request.getSlug(), id);
        applyRequestToEntity(section, request);

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
        sectionRepository.delete(section);
    }

    private Section getEntityById(Long id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + id));
    }

    private void validateSlugUniqueness(String slug, Long currentSectionId) {
        sectionRepository.findBySlug(slug).ifPresent(existing -> {
            if (currentSectionId == null || !existing.getId().equals(currentSectionId)) {
                throw new ConflictException("Section slug already exists: " + slug);
            }
        });
    }

    private void applyRequestToEntity(Section section, SectionRequest request) {
        section.setSlug(request.getSlug());
        section.setNamePt(request.getNamePt());
        section.setNameEn(request.getNameEn());
        section.setDescriptionPt(request.getDescriptionPt());
        section.setDescriptionEn(request.getDescriptionEn());
        section.setSectionType(request.getSectionType());
        section.setCoverImageUrl(request.getCoverImageUrl());
        section.setCoverVideoUrl(request.getCoverVideoUrl());
        section.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        section.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
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
                .isActive(section.getIsActive())
                .sortOrder(section.getSortOrder())
                .createdAt(section.getCreatedAt())
                .updatedAt(section.getUpdatedAt())
                .build();
    }
}