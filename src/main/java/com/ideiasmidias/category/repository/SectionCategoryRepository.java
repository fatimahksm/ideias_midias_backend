package com.ideiasmidias.category.repository;

import com.ideiasmidias.category.entity.SectionCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionCategoryRepository extends JpaRepository<SectionCategory, Long> {

    List<SectionCategory> findAllByOrderBySortOrderAscIdAsc();

    List<SectionCategory> findAllByIsActiveTrueOrderBySortOrderAscIdAsc();

    List<SectionCategory> findAllBySection_IdOrderBySortOrderAscIdAsc(Long sectionId);

    List<SectionCategory> findAllBySection_IdAndIsActiveTrueOrderBySortOrderAscIdAsc(Long sectionId);
}