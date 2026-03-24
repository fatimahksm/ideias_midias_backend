package com.ideiasmidias.section.service;

import com.ideiasmidias.category.dto.SectionCategoryResponse;
import com.ideiasmidias.category.service.SectionCategoryService;
import com.ideiasmidias.common.enums.SectionType;
import com.ideiasmidias.contentblock.dto.SectionContentBlockResponse;
import com.ideiasmidias.contentblock.service.SectionContentBlockService;
import com.ideiasmidias.item.dto.SectionItemMediaResponse;
import com.ideiasmidias.item.dto.SectionItemResponse;
import com.ideiasmidias.item.service.SectionItemMediaService;
import com.ideiasmidias.item.service.SectionItemService;
import com.ideiasmidias.portfolio.dto.PortfolioProjectMediaResponse;
import com.ideiasmidias.portfolio.dto.PortfolioProjectResponse;
import com.ideiasmidias.portfolio.service.PortfolioProjectMediaService;
import com.ideiasmidias.portfolio.service.PortfolioProjectService;
import com.ideiasmidias.section.dto.PortfolioProjectDetailsResponse;
import com.ideiasmidias.section.dto.SectionCategoryDetailsResponse;
import com.ideiasmidias.section.dto.SectionDetailsResponse;
import com.ideiasmidias.section.dto.SectionItemDetailsResponse;
import com.ideiasmidias.section.dto.SectionResponse;
import com.ideiasmidias.sectionattribute.dto.SectionAttributeDefinitionResponse;
import com.ideiasmidias.sectionattribute.service.SectionAttributeDefinitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SectionDetailsServiceImpl implements SectionDetailsService {

    private final SectionService sectionService;
    private final SectionContentBlockService sectionContentBlockService;
    private final SectionCategoryService sectionCategoryService;
    private final SectionItemService sectionItemService;
    private final SectionItemMediaService sectionItemMediaService;
    private final PortfolioProjectService portfolioProjectService;
    private final PortfolioProjectMediaService portfolioProjectMediaService;
    private final SectionAttributeDefinitionService sectionAttributeDefinitionService;

    @Override
    public SectionDetailsResponse getAdminDetailsById(Long sectionId) {
        SectionResponse section = sectionService.getById(sectionId);
        return buildDetails(section, false);
    }

    @Override
    public SectionDetailsResponse getPublicDetailsBySlug(String slug) {
        SectionResponse section = sectionService.getActiveBySlug(slug);
        return buildDetails(section, true);
    }

    private SectionDetailsResponse buildDetails(SectionResponse section, boolean onlyActive) {
        List<SectionContentBlockResponse> contentBlocks = onlyActive
                ? sectionContentBlockService.getActiveBySection(section.getId())
                : sectionContentBlockService.getBySection(section.getId());

        List<SectionCategoryDetailsResponse> categories = Collections.emptyList();
        List<SectionItemDetailsResponse> directItems = Collections.emptyList();
        List<PortfolioProjectDetailsResponse> portfolioProjects = Collections.emptyList();
        List<SectionAttributeDefinitionResponse> attributeDefinitions = Collections.emptyList();

        if (section.getSectionType() == SectionType.CATEGORY_ITEMS) {
            List<SectionCategoryResponse> categoryResponses = onlyActive
                    ? sectionCategoryService.getActiveBySection(section.getId())
                    : sectionCategoryService.getBySection(section.getId());

            categories = categoryResponses.stream()
                    .map(category -> SectionCategoryDetailsResponse.builder()
                            .category(category)
                            .items(getItemDetailsByCategory(category.getId(), onlyActive))
                            .build())
                    .toList();
        }

        if (section.getSectionType() == SectionType.DIRECT_ITEMS) {
            List<SectionItemResponse> itemResponses = onlyActive
                    ? sectionItemService.getActiveDirectItemsBySection(section.getId())
                    : sectionItemService.getDirectItemsBySection(section.getId());

            directItems = itemResponses.stream()
                    .map(item -> buildItemDetails(item, onlyActive))
                    .toList();

            attributeDefinitions = sectionAttributeDefinitionService.getBySection(section.getId(), onlyActive);
        }

        if (section.getSectionType() == SectionType.CATEGORY_ITEMS) {
            attributeDefinitions = sectionAttributeDefinitionService.getBySection(section.getId(), onlyActive);
        }

        if (section.getSectionType() == SectionType.PORTFOLIO) {
            List<PortfolioProjectResponse> projectResponses = onlyActive
                    ? portfolioProjectService.getActiveBySection(section.getId())
                    : portfolioProjectService.getBySection(section.getId());

            portfolioProjects = projectResponses.stream()
                    .map(project -> buildProjectDetails(project, onlyActive))
                    .toList();
        }

        return SectionDetailsResponse.builder()
                .section(section)
                .contentBlocks(contentBlocks)
                .categories(categories)
                .directItems(directItems)
                .portfolioProjects(portfolioProjects)
                .attributeDefinitions(attributeDefinitions)
                .build();
    }

    private List<SectionItemDetailsResponse> getItemDetailsByCategory(Long categoryId, boolean onlyActive) {
        List<SectionItemResponse> items = onlyActive
                ? sectionItemService.getActiveByCategory(categoryId)
                : sectionItemService.getByCategory(categoryId);

        return items.stream()
                .map(item -> buildItemDetails(item, onlyActive))
                .toList();
    }

    private SectionItemDetailsResponse buildItemDetails(SectionItemResponse item, boolean onlyActive) {
        List<SectionItemMediaResponse> media = onlyActive
                ? sectionItemMediaService.getActiveByItem(item.getId())
                : sectionItemMediaService.getByItem(item.getId());

        return SectionItemDetailsResponse.builder()
                .item(item)
                .media(media)
                .build();
    }

    private PortfolioProjectDetailsResponse buildProjectDetails(PortfolioProjectResponse project, boolean onlyActive) {
        List<PortfolioProjectMediaResponse> media = onlyActive
                ? portfolioProjectMediaService.getActiveByProject(project.getId())
                : portfolioProjectMediaService.getByProject(project.getId());

        return PortfolioProjectDetailsResponse.builder()
                .project(project)
                .media(media)
                .build();
    }
}
