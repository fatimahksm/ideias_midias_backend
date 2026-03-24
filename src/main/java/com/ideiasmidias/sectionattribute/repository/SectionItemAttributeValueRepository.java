package com.ideiasmidias.sectionattribute.repository;

import com.ideiasmidias.sectionattribute.entity.SectionItemAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionItemAttributeValueRepository extends JpaRepository<SectionItemAttributeValue, Long> {

    List<SectionItemAttributeValue> findAllBySectionItem_Id(Long sectionItemId);

    long countBySectionItem_Id(Long sectionItemId);

    long countByAttributeDefinition_Id(Long attributeDefinitionId);

    void deleteAllBySectionItem_Id(Long sectionItemId);
}
