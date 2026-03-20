package com.ideiasmidias.contentblock.service;

import com.ideiasmidias.common.enums.ContentBlockType;
import com.ideiasmidias.contentblock.dto.SectionContentBlockRequest;
import com.ideiasmidias.contentblock.dto.SectionContentBlockResponse;

import java.util.List;

public interface SectionContentBlockService {

    SectionContentBlockResponse create(SectionContentBlockRequest request);

    SectionContentBlockResponse update(Long id, SectionContentBlockRequest request);

    SectionContentBlockResponse getById(Long id);

    List<SectionContentBlockResponse> getAll();

    List<SectionContentBlockResponse> getAllActive();

    List<SectionContentBlockResponse> getBySection(Long sectionId);

    List<SectionContentBlockResponse> getActiveBySection(Long sectionId);

    List<SectionContentBlockResponse> getBySectionAndType(Long sectionId, ContentBlockType blockType);

    List<SectionContentBlockResponse> getActiveBySectionAndType(Long sectionId, ContentBlockType blockType);

    void delete(Long id);
}