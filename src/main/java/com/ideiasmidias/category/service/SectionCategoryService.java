package com.ideiasmidias.category.service;

import com.ideiasmidias.category.dto.SectionCategoryRequest;
import com.ideiasmidias.category.dto.SectionCategoryResponse;

import java.util.List;

public interface SectionCategoryService {

    SectionCategoryResponse create(SectionCategoryRequest request);

    SectionCategoryResponse update(Long id, SectionCategoryRequest request);

    SectionCategoryResponse getById(Long id);

    List<SectionCategoryResponse> getAll();

    List<SectionCategoryResponse> getAllActive();

    List<SectionCategoryResponse> getBySection(Long sectionId);

    List<SectionCategoryResponse> getActiveBySection(Long sectionId);

    void delete(Long id);
}