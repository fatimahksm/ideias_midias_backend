package com.ideiasmidias.item.service;

import com.ideiasmidias.common.enums.MediaType;
import com.ideiasmidias.common.exception.ResourceNotFoundException;
import com.ideiasmidias.item.dto.SectionItemMediaRequest;
import com.ideiasmidias.item.dto.SectionItemMediaResponse;
import com.ideiasmidias.item.entity.SectionItem;
import com.ideiasmidias.item.entity.SectionItemMedia;
import com.ideiasmidias.item.repository.SectionItemMediaRepository;
import com.ideiasmidias.item.repository.SectionItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SectionItemMediaServiceImpl implements SectionItemMediaService {

    private final SectionItemMediaRepository sectionItemMediaRepository;
    private final SectionItemRepository sectionItemRepository;

    @Override
    public SectionItemMediaResponse create(SectionItemMediaRequest request) {
        SectionItem item = getItemById(request.getItemId());

        SectionItemMedia media = new SectionItemMedia();
        applyRequestToEntity(media, request, item);

        SectionItemMedia saved = sectionItemMediaRepository.save(media);
        return mapToResponse(saved);
    }

    @Override
    public SectionItemMediaResponse update(Long id, SectionItemMediaRequest request) {
        SectionItemMedia media = getEntityById(id);
        SectionItem item = getItemById(request.getItemId());

        applyRequestToEntity(media, request, item);

        SectionItemMedia saved = sectionItemMediaRepository.save(media);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public SectionItemMediaResponse getById(Long id) {
        return mapToResponse(getEntityById(id));
    }

    @Override
    @Transactional
    public List<SectionItemMediaResponse> getAll() {
        return sectionItemMediaRepository.findAllByOrderBySortOrderAscIdAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<SectionItemMediaResponse> getAllActive() {
        return sectionItemMediaRepository.findAllByIsActiveTrueOrderBySortOrderAscIdAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<SectionItemMediaResponse> getByItem(Long itemId) {
        return sectionItemMediaRepository.findAllByItem_IdOrderBySortOrderAscIdAsc(itemId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<SectionItemMediaResponse> getActiveByItem(Long itemId) {
        return sectionItemMediaRepository.findAllByItem_IdAndIsActiveTrueOrderBySortOrderAscIdAsc(itemId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<SectionItemMediaResponse> getByItemAndType(Long itemId, MediaType mediaType) {
        return sectionItemMediaRepository.findAllByItem_IdAndMediaTypeOrderBySortOrderAscIdAsc(itemId, mediaType)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<SectionItemMediaResponse> getActiveByItemAndType(Long itemId, MediaType mediaType) {
        return sectionItemMediaRepository.findAllByItem_IdAndMediaTypeAndIsActiveTrueOrderBySortOrderAscIdAsc(itemId, mediaType)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        SectionItemMedia media = getEntityById(id);
        sectionItemMediaRepository.delete(media);
    }

    private SectionItemMedia getEntityById(Long id) {
        return sectionItemMediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section item media not found with id: " + id));
    }

    private SectionItem getItemById(Long id) {
        return sectionItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section item not found with id: " + id));
    }

    private void applyRequestToEntity(SectionItemMedia media, SectionItemMediaRequest request, SectionItem item) {
        media.setItem(item);
        media.setMediaType(request.getMediaType());
        media.setMediaUrl(request.getMediaUrl());
        media.setThumbnailUrl(request.getThumbnailUrl());
        media.setAltTextPt(request.getAltTextPt());
        media.setAltTextEn(request.getAltTextEn());
        media.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        media.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
    }

    private SectionItemMediaResponse mapToResponse(SectionItemMedia media) {
        return SectionItemMediaResponse.builder()
                .id(media.getId())
                .itemId(media.getItem().getId())
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