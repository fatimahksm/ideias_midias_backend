package com.ideiasmidias.section.dto;

import com.ideiasmidias.contentblock.dto.SectionContentBlockResponse;
import com.ideiasmidias.sectionattribute.dto.SectionAttributeDefinitionResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SectionDetailsResponse {

    private SectionResponse section;
    private List<SectionContentBlockResponse> contentBlocks;
    private List<SectionCategoryDetailsResponse> categories;
    private List<SectionItemDetailsResponse> directItems;
    private List<PortfolioProjectDetailsResponse> portfolioProjects;
    private List<SectionAttributeDefinitionResponse> attributeDefinitions;
}
