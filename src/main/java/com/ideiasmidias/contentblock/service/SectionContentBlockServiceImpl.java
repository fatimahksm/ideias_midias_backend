package com.ideiasmidias.contentblock.service;

import com.ideiasmidias.common.enums.ContentBlockType;
import com.ideiasmidias.common.enums.SectionType;
import com.ideiasmidias.common.exception.BadRequestException;
import com.ideiasmidias.common.exception.ResourceNotFoundException;
import com.ideiasmidias.contentblock.dto.SectionContentBlockRequest;
import com.ideiasmidias.contentblock.dto.SectionContentBlockResponse;
import com.ideiasmidias.contentblock.entity.SectionContentBlock;
import com.ideiasmidias.contentblock.repository.SectionContentBlockRepository;
import com.ideiasmidias.section.entity.Section;
import com.ideiasmidias.section.repository.SectionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SectionContentBlockServiceImpl implements SectionContentBlockService {

    private final SectionContentBlockRepository sectionContentBlockRepository;
    private final SectionRepository sectionRepository;

    @Override
    public SectionContentBlockResponse create(SectionContentBlockRequest request) {
        Section section = getValidContentSection(request.getSectionId());

        SectionContentBlock block = new SectionContentBlock();
        applyRequestToEntity(block, request, section);

        SectionContentBlock saved = sectionContentBlockRepository.save(block);
        return mapToResponse(saved);
    }

    @Override
    public SectionContentBlockResponse update(Long id, SectionContentBlockRequest request) {
        SectionContentBlock block = getEntityById(id);
        Section section = getValidContentSection(request.getSectionId());

        applyRequestToEntity(block, request, section);

        SectionContentBlock saved = sectionContentBlockRepository.save(block);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public SectionContentBlockResponse getById(Long id) {
        return mapToResponse(getEntityById(id));
    }

    @Override
    @Transactional
    public List<SectionContentBlockResponse> getAll() {
        return sectionContentBlockRepository.findAllByOrderBySortOrderAscIdAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<SectionContentBlockResponse> getAllActive() {
        return sectionContentBlockRepository.findAllByIsActiveTrueOrderBySortOrderAscIdAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<SectionContentBlockResponse> getBySection(Long sectionId) {
        return sectionContentBlockRepository.findAllBySection_IdOrderBySortOrderAscIdAsc(sectionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<SectionContentBlockResponse> getActiveBySection(Long sectionId) {
        return sectionContentBlockRepository.findAllBySection_IdAndIsActiveTrueOrderBySortOrderAscIdAsc(sectionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<SectionContentBlockResponse> getBySectionAndType(Long sectionId, ContentBlockType blockType) {
        return sectionContentBlockRepository.findAllBySection_IdAndBlockTypeOrderBySortOrderAscIdAsc(sectionId, blockType)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<SectionContentBlockResponse> getActiveBySectionAndType(Long sectionId, ContentBlockType blockType) {
        return sectionContentBlockRepository.findAllBySection_IdAndBlockTypeAndIsActiveTrueOrderBySortOrderAscIdAsc(sectionId, blockType)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        SectionContentBlock block = getEntityById(id);
        sectionContentBlockRepository.delete(block);
    }

    private SectionContentBlock getEntityById(Long id) {
        return sectionContentBlockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section content block not found with id: " + id));
    }

    private Section getValidContentSection(Long sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));

        if (section.getSectionType() != SectionType.CONTENT) {
            throw new BadRequestException("Content blocks can only be added to sections of type CONTENT");
        }

        return section;
    }

    private void applyRequestToEntity(SectionContentBlock block, SectionContentBlockRequest request, Section section) {
        block.setSection(section);
        block.setBlockType(request.getBlockType());
        block.setTitlePt(request.getTitlePt());
        block.setTitleEn(request.getTitleEn());
        block.setSubtitlePt(request.getSubtitlePt());
        block.setSubtitleEn(request.getSubtitleEn());
        block.setContentPt(request.getContentPt());
        block.setContentEn(request.getContentEn());
        block.setImageUrl(request.getImageUrl());
        block.setVideoUrl(request.getVideoUrl());
        block.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        block.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
    }

    private SectionContentBlockResponse mapToResponse(SectionContentBlock block) {
        return SectionContentBlockResponse.builder()
                .id(block.getId())
                .sectionId(block.getSection().getId())
                .blockType(block.getBlockType())
                .titlePt(block.getTitlePt())
                .titleEn(block.getTitleEn())
                .subtitlePt(block.getSubtitlePt())
                .subtitleEn(block.getSubtitleEn())
                .contentPt(block.getContentPt())
                .contentEn(block.getContentEn())
                .imageUrl(block.getImageUrl())
                .videoUrl(block.getVideoUrl())
                .isActive(block.getIsActive())
                .sortOrder(block.getSortOrder())
                .createdAt(block.getCreatedAt())
                .updatedAt(block.getUpdatedAt())
                .build();
    }
}