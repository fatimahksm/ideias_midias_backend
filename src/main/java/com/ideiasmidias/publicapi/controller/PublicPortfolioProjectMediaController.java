package com.ideiasmidias.publicapi.controller;

import com.ideiasmidias.common.enums.MediaType;
import com.ideiasmidias.common.enums.SectionType;
import com.ideiasmidias.common.exception.BadRequestException;
import com.ideiasmidias.common.exception.ResourceNotFoundException;
import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.portfolio.dto.PortfolioProjectMediaResponse;
import com.ideiasmidias.portfolio.dto.PortfolioProjectResponse;
import com.ideiasmidias.portfolio.service.PortfolioProjectMediaService;
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
public class PublicPortfolioProjectMediaController {

    private final PortfolioProjectService portfolioProjectService;
    private final PortfolioProjectMediaService portfolioProjectMediaService;
    private final SectionService sectionService;

    @GetMapping("/projects/{projectId}/media")
    public ResponseEntity<ApiResponse<List<PortfolioProjectMediaResponse>>> getActiveMediaByProject(
            @PathVariable Long projectId,
            @RequestParam(required = false) MediaType mediaType
    ) {
        PortfolioProjectResponse project = getActivePortfolioProjectOrThrow(projectId);

        List<PortfolioProjectMediaResponse> response =
                mediaType == null
                        ? portfolioProjectMediaService.getActiveByProject(project.getId())
                        : portfolioProjectMediaService.getActiveByProjectAndType(project.getId(), mediaType);

        return ResponseEntity.ok(
                ApiResponse.<List<PortfolioProjectMediaResponse>>builder()
                        .success(true)
                        .message("Public active portfolio project media fetched successfully")
                        .data(response)
                        .build()
        );
    }

    private PortfolioProjectResponse getActivePortfolioProjectOrThrow(Long projectId) {
        PortfolioProjectResponse project = portfolioProjectService.getById(projectId);

        if (!Boolean.TRUE.equals(project.getIsActive())) {
            throw new ResourceNotFoundException("Active portfolio project not found with id: " + projectId);
        }

        SectionResponse section = sectionService.getById(project.getSectionId());

        if (!Boolean.TRUE.equals(section.getIsActive())) {
            throw new ResourceNotFoundException("Active portfolio section not found for project id: " + projectId);
        }

        if (section.getSectionType() != SectionType.PORTFOLIO) {
            throw new BadRequestException("Project does not belong to a PORTFOLIO section");
        }

        return project;
    }
}