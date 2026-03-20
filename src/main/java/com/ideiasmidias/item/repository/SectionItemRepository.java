package com.ideiasmidias.item.repository;

import com.ideiasmidias.item.entity.SectionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionItemRepository extends JpaRepository<SectionItem, Long> {

    List<SectionItem> findAllByOrderBySortOrderAscIdAsc();

    List<SectionItem> findAllByIsActiveTrueOrderBySortOrderAscIdAsc();

    List<SectionItem> findAllBySection_IdOrderBySortOrderAscIdAsc(Long sectionId);

    List<SectionItem> findAllBySection_IdAndIsActiveTrueOrderBySortOrderAscIdAsc(Long sectionId);

    List<SectionItem> findAllByCategory_IdOrderBySortOrderAscIdAsc(Long categoryId);

    List<SectionItem> findAllByCategory_IdAndIsActiveTrueOrderBySortOrderAscIdAsc(Long categoryId);

    List<SectionItem> findAllBySection_IdAndCategoryIsNullOrderBySortOrderAscIdAsc(Long sectionId);

    List<SectionItem> findAllBySection_IdAndCategoryIsNullAndIsActiveTrueOrderBySortOrderAscIdAsc(Long sectionId);

    List<SectionItem> findAllBySection_IdAndIsFeaturedTrueOrderBySortOrderAscIdAsc(Long sectionId);

    List<SectionItem> findAllBySection_IdAndIsFeaturedTrueAndIsActiveTrueOrderBySortOrderAscIdAsc(Long sectionId);
}