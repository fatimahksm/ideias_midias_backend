package com.ideiasmidias.homecard.service;

import com.ideiasmidias.common.exception.ResourceNotFoundException;
import com.ideiasmidias.homecard.dto.HomeCardRequest;
import com.ideiasmidias.homecard.dto.HomeCardResponse;
import com.ideiasmidias.homecard.entity.HomeCard;
import com.ideiasmidias.homecard.repository.HomeCardRepository;
import com.ideiasmidias.section.entity.Section;
import com.ideiasmidias.section.repository.SectionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HomeCardServiceImpl implements HomeCardService {

    private final HomeCardRepository homeCardRepository;
    private final SectionRepository sectionRepository;

    @Override
    public HomeCardResponse create(HomeCardRequest request) {
        Section section = getSectionById(request.getSectionId());

        HomeCard homeCard = new HomeCard();
        applyRequestToEntity(homeCard, request, section);

        HomeCard saved = homeCardRepository.save(homeCard);
        return mapToResponse(saved);
    }

    @Override
    public HomeCardResponse update(Long id, HomeCardRequest request) {
        HomeCard homeCard = getEntityById(id);
        Section section = getSectionById(request.getSectionId());

        applyRequestToEntity(homeCard, request, section);

        HomeCard saved = homeCardRepository.save(homeCard);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public HomeCardResponse getById(Long id) {
        return mapToResponse(getEntityById(id));
    }

    @Override
    @Transactional
    public List<HomeCardResponse> getAll() {
        return homeCardRepository.findAllByOrderBySortOrderAscIdAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<HomeCardResponse> getAllActive() {
        return homeCardRepository.findAllByIsActiveTrueOrderBySortOrderAscIdAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<HomeCardResponse> getBySection(Long sectionId) {
        return homeCardRepository.findAllBySectionIdOrderBySortOrderAscIdAsc(sectionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<HomeCardResponse> getActiveBySection(Long sectionId) {
        return homeCardRepository.findAllBySectionIdAndIsActiveTrueOrderBySortOrderAscIdAsc(sectionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        HomeCard homeCard = getEntityById(id);
        homeCardRepository.delete(homeCard);
    }

    private HomeCard getEntityById(Long id) {
        return homeCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Home card not found with id: " + id));
    }

    private Section getSectionById(Long id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + id));
    }

    private void applyRequestToEntity(HomeCard homeCard, HomeCardRequest request, Section section) {
        homeCard.setSection(section);
        homeCard.setTitlePt(request.getTitlePt());
        homeCard.setTitleEn(request.getTitleEn());
        homeCard.setShortDescriptionPt(request.getShortDescriptionPt());
        homeCard.setShortDescriptionEn(request.getShortDescriptionEn());
        homeCard.setImageUrl(request.getImageUrl());
        homeCard.setIconName(request.getIconName());
        homeCard.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        homeCard.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
    }

    private HomeCardResponse mapToResponse(HomeCard homeCard) {
        return HomeCardResponse.builder()
                .id(homeCard.getId())
                .sectionId(homeCard.getSection().getId())
                .sectionSlug(homeCard.getSection().getSlug()) // ADD THIS
                .titlePt(homeCard.getTitlePt())
                .titleEn(homeCard.getTitleEn())
                .shortDescriptionPt(homeCard.getShortDescriptionPt())
                .shortDescriptionEn(homeCard.getShortDescriptionEn())
                .imageUrl(homeCard.getImageUrl())
                .iconName(homeCard.getIconName())
                .isActive(homeCard.getIsActive())
                .sortOrder(homeCard.getSortOrder())
                .createdAt(homeCard.getCreatedAt())
                .updatedAt(homeCard.getUpdatedAt())
                .build();
    }
}