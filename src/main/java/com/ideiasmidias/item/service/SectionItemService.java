package com.ideiasmidias.item.service;

import com.ideiasmidias.item.dto.SectionItemRequest;
import com.ideiasmidias.common.response.PageResponse;
import com.ideiasmidias.item.dto.SectionItemResponse;
import com.ideiasmidias.item.dto.SectionItemStatsResponse;

import java.util.List;

public interface SectionItemService {

    SectionItemResponse create(SectionItemRequest request);

    SectionItemResponse update(Long id, SectionItemRequest request);

    SectionItemResponse getById(Long id);

    List<SectionItemResponse> getAll();

    /** Counts for the stat cards, scoped the same way the listing is. */
    SectionItemStatsResponse stats(Long sectionId, Long categoryId);

    /**
     * One filtered, sorted page of items. Every argument except the paging
     * ones is optional.
     */
    PageResponse<SectionItemResponse> search(
            Long sectionId,
            Long categoryId,
            boolean onlyUncategorized,
            String status,
            Boolean isFeatured,
            String search,
            String sort,
            int page,
            int size
    );

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