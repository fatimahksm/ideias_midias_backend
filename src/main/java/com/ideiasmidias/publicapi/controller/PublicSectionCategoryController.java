package com.ideiasmidias.publicapi.controller;

import com.ideiasmidias.category.dto.SectionCategoryResponse;
import com.ideiasmidias.category.service.SectionCategoryService;
import com.ideiasmidias.common.enums.SectionType;
import com.ideiasmidias.common.exception.BadRequestException;
import com.ideiasmidias.common.exception.ResourceNotFoundException;
import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.section.dto.SectionResponse;
import com.ideiasmidias.section.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/sections")
@RequiredArgsConstructor
public class PublicSectionCategoryController {

    private final SectionService sectionService;
    private final SectionCategoryService sectionCategoryService;

    @GetMapping("/{sectionId}/categories")
    public ResponseEntity<ApiResponse<List<SectionCategoryResponse>>> getActiveCategoriesBySection(
            @PathVariable Long sectionId
    ) {
        SectionResponse section = getActiveSectionOrThrow(sectionId);

        if (section.getSectionType() != SectionType.CATEGORY_ITEMS) {
            throw new BadRequestException("Categories are only available for sections of type CATEGORY_ITEMS");
        }

        List<SectionCategoryResponse> response = sectionCategoryService.getActiveBySection(sectionId);

        return ResponseEntity.ok(
                ApiResponse.<List<SectionCategoryResponse>>builder()
                        .success(true)
                        .message("Public active section categories fetched successfully")
                        .data(response)
                        .build()
        );
    }

    private SectionResponse getActiveSectionOrThrow(Long sectionId) {
        SectionResponse section = sectionService.getById(sectionId);

        if (!Boolean.TRUE.equals(section.getIsActive())) {
            throw new ResourceNotFoundException("Active section not found with id: " + sectionId);
        }

        return section;
    }
}