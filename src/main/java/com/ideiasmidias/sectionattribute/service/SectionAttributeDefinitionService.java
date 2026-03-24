package com.ideiasmidias.sectionattribute.service;

import com.ideiasmidias.sectionattribute.dto.SectionAttributeDefinitionRequest;
import com.ideiasmidias.sectionattribute.dto.SectionAttributeDefinitionResponse;
import com.ideiasmidias.sectionattribute.dto.SectionItemAttributeValueRequest;
import com.ideiasmidias.sectionattribute.dto.SectionItemAttributeValueResponse;
import com.ideiasmidias.sectionattribute.entity.SectionItemAttributeValue;

import java.util.List;

public interface SectionAttributeDefinitionService {

    SectionAttributeDefinitionResponse create(SectionAttributeDefinitionRequest request);

    SectionAttributeDefinitionResponse update(Long id, SectionAttributeDefinitionRequest request);

    SectionAttributeDefinitionResponse getById(Long id);

    List<SectionAttributeDefinitionResponse> getBySection(Long sectionId, boolean onlyActive);

    void delete(Long id);

    List<SectionItemAttributeValue> replaceValuesForItem(Long sectionId, Long sectionItemId, List<SectionItemAttributeValueRequest> values);

    List<SectionItemAttributeValueResponse> toValueResponses(List<SectionItemAttributeValue> values);
}
