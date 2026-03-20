package com.ideiasmidias.section.service;

import com.ideiasmidias.common.enums.SectionType;
import com.ideiasmidias.section.dto.SectionRequest;
import com.ideiasmidias.section.dto.SectionResponse;

import java.util.List;

public interface SectionService {

    SectionResponse create(SectionRequest request);

    SectionResponse update(Long id, SectionRequest request);

    SectionResponse getById(Long id);

    SectionResponse getBySlug(String slug);

    SectionResponse getActiveBySlug(String slug);

    List<SectionResponse> getAll();

    List<SectionResponse> getAllActive();

    List<SectionResponse> getByType(SectionType sectionType);

    List<SectionResponse> getActiveByType(SectionType sectionType);

    void delete(Long id);
}