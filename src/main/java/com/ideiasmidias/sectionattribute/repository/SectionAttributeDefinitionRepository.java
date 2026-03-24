package com.ideiasmidias.sectionattribute.repository;

import com.ideiasmidias.sectionattribute.entity.SectionAttributeDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SectionAttributeDefinitionRepository extends JpaRepository<SectionAttributeDefinition, Long> {

    List<SectionAttributeDefinition> findAllBySection_IdOrderBySortOrderAscIdAsc(Long sectionId);

    List<SectionAttributeDefinition> findAllBySection_IdAndIsActiveTrueOrderBySortOrderAscIdAsc(Long sectionId);

    Optional<SectionAttributeDefinition> findBySection_IdAndCode(Long sectionId, String code);

    long countBySection_Id(Long sectionId);
}
