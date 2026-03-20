package com.ideiasmidias.contentblock.repository;

import com.ideiasmidias.common.enums.ContentBlockType;
import com.ideiasmidias.contentblock.entity.SectionContentBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionContentBlockRepository extends JpaRepository<SectionContentBlock, Long> {

    List<SectionContentBlock> findAllByOrderBySortOrderAscIdAsc();

    List<SectionContentBlock> findAllByIsActiveTrueOrderBySortOrderAscIdAsc();

    List<SectionContentBlock> findAllBySection_IdOrderBySortOrderAscIdAsc(Long sectionId);

    List<SectionContentBlock> findAllBySection_IdAndIsActiveTrueOrderBySortOrderAscIdAsc(Long sectionId);

    List<SectionContentBlock> findAllBySection_IdAndBlockTypeOrderBySortOrderAscIdAsc(Long sectionId, ContentBlockType blockType);

    List<SectionContentBlock> findAllBySection_IdAndBlockTypeAndIsActiveTrueOrderBySortOrderAscIdAsc(Long sectionId, ContentBlockType blockType);
}