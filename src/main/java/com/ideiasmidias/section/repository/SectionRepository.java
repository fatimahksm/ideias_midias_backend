package com.ideiasmidias.section.repository;

import com.ideiasmidias.common.enums.SectionType;
import com.ideiasmidias.section.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Long> {

    long countByIsActiveTrue();

    Optional<Section> findBySlug(String slug);

    Optional<Section> findBySlugAndIsActiveTrue(String slug);

    boolean existsBySlug(String slug);

    List<Section> findAllByOrderBySortOrderAscIdAsc();

    List<Section> findAllByIsActiveTrueOrderBySortOrderAscIdAsc();

    List<Section> findAllBySectionTypeOrderBySortOrderAscIdAsc(SectionType sectionType);

    List<Section> findAllBySectionTypeAndIsActiveTrueOrderBySortOrderAscIdAsc(SectionType sectionType);
}