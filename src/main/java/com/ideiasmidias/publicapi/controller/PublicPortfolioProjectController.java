package com.ideiasmidias.publicapi.controller;

import com.ideiasmidias.common.enums.SectionType;
import com.ideiasmidias.common.exception.BadRequestException;
import com.ideiasmidias.common.exception.ResourceNotFoundException;
import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.portfolio.dto.PortfolioProjectResponse;
import com.ideiasmidias.portfolio.service.PortfolioProjectService;
import com.ideiasmidias.section.dto.SectionResponse;
import com.ideiasmidias.section.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/portfolio")
@RequiredArgsConstructor
public class PublicPortfolioProjectController {

    private final SectionService sectionService;
    private final PortfolioProjectService portfolioProjectService;

    @GetMapping("/sections/{sectionId}/projects")
    public ResponseEntity<ApiResponse<List<PortfolioProjectResponse>>> getActiveProjectsBySection(
            @PathVariable Long sectionId
    ) {
        SectionResponse section = getActivePortfolioSectionOrThrow(sectionId);

        List<PortfolioProjectResponse> response = portfolioProjectService.getActiveBySection(section.getId());

        return ResponseEntity.ok(
                ApiResponse.<List<PortfolioProjectResponse>>builder()
                        .success(true)
                        .message("Public active portfolio projects fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/sections/{sectionId}/projects/featured")
    public ResponseEntity<ApiResponse<List<PortfolioProjectResponse>>> getActiveFeaturedProjectsBySection(
            @PathVariable Long sectionId
    ) {
        SectionResponse section = getActivePortfolioSectionOrThrow(sectionId);

        List<PortfolioProjectResponse> response = portfolioProjectService.getActiveFeaturedBySection(section.getId());

        return ResponseEntity.ok(
                ApiResponse.<List<PortfolioProjectResponse>>builder()
                        .success(true)
                        .message("Public active featured portfolio projects fetched successfully")
                        .data(response)
                        .build()
        );
    }

    private SectionResponse getActivePortfolioSectionOrThrow(Long sectionId) {
        SectionResponse section = sectionService.getById(sectionId);

        if (!Boolean.TRUE.equals(section.getIsActive())) {
            throw new ResourceNotFoundException("Active portfolio section not found with id: " + sectionId);
        }

        if (section.getSectionType() != SectionType.PORTFOLIO) {
            throw new BadRequestException("Portfolio projects are only available for sections of type PORTFOLIO");
        }

        return section;
    }
}