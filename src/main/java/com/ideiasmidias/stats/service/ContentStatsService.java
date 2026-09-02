package com.ideiasmidias.stats.service;

import com.ideiasmidias.contact.repository.ContactMethodRepository;
import com.ideiasmidias.category.repository.SectionCategoryRepository;
import com.ideiasmidias.contentblock.repository.SectionContentBlockRepository;
import com.ideiasmidias.homecard.repository.HomeCardRepository;
import com.ideiasmidias.item.repository.SectionItemRepository;
import com.ideiasmidias.media.repository.MediaLibraryRepository;
import com.ideiasmidias.portfolio.repository.PortfolioProjectRepository;
import com.ideiasmidias.section.repository.SectionRepository;
import com.ideiasmidias.stats.dto.ContentStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Counts for the dashboard. The dashboard used to fetch every row of nine
 * tables just to show how many there were; these are nine COUNT queries.
 */
@Service
@RequiredArgsConstructor
public class ContentStatsService {

    private final SectionRepository sectionRepository;
    private final SectionCategoryRepository categoryRepository;
    private final SectionItemRepository itemRepository;
    private final PortfolioProjectRepository portfolioProjectRepository;
    private final SectionContentBlockRepository contentBlockRepository;
    private final MediaLibraryRepository mediaLibraryRepository;
    private final HomeCardRepository homeCardRepository;
    private final ContactMethodRepository contactMethodRepository;

    public ContentStatsResponse getSummary() {
        return ContentStatsResponse.builder()
                .sections(sectionRepository.count())
                .activeSections(sectionRepository.countByIsActiveTrue())
                .categories(categoryRepository.count())
                .items(itemRepository.count())
                .portfolioProjects(portfolioProjectRepository.count())
                .contentBlocks(contentBlockRepository.count())
                .mediaFiles(mediaLibraryRepository.count())
                .homeCards(homeCardRepository.count())
                .contactMethods(contactMethodRepository.count())
                .build();
    }
}
