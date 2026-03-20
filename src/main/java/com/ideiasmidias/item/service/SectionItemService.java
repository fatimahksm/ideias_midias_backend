package com.ideiasmidias.item.service;

import com.ideiasmidias.item.dto.SectionItemRequest;
import com.ideiasmidias.item.dto.SectionItemResponse;

import java.util.List;

public interface SectionItemService {

    SectionItemResponse create(SectionItemRequest request);

    SectionItemResponse update(Long id, SectionItemRequest request);

    SectionItemResponse getById(Long id);

    List<SectionItemResponse> getAll();

    List<SectionItemResponse> getAllActive();

    List<SectionItemResponse> getBySection(Long sectionId);

    List<SectionItemResponse> getActiveBySection(Long sectionId);

    List<SectionItemResponse> getByCategory(Long categoryId);

    List<SectionItemResponse> getActiveByCategory(Long categoryId);

    List<SectionItemResponse> getDirectItemsBySection(Long sectionId);

    List<SectionItemResponse> getActiveDirectItemsBySection(Long sectionId);

    List<SectionItemResponse> getFeaturedBySection(Long sectionId);

    List<SectionItemResponse> getActiveFeaturedBySection(Long sectionId);

    void delete(Long id);
}