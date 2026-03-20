package com.ideiasmidias.publicapi.controller;

import com.ideiasmidias.common.enums.SectionType;
import com.ideiasmidias.common.exception.BadRequestException;
import com.ideiasmidias.common.exception.ResourceNotFoundException;
import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.contentblock.dto.SectionContentBlockResponse;
import com.ideiasmidias.contentblock.service.SectionContentBlockService;
import com.ideiasmidias.section.dto.SectionResponse;
import com.ideiasmidias.section.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/sections")
@RequiredArgsConstructor
public class PublicSectionContentBlockController {

    private final SectionService sectionService;
    private final SectionContentBlockService sectionContentBlockService;

    @GetMapping("/{sectionId}/content-blocks")
    public ResponseEntity<ApiResponse<List<SectionContentBlockResponse>>> getActiveContentBlocksBySection(
            @PathVariable Long sectionId
    ) {
        SectionResponse section = getActiveSectionOrThrow(sectionId);

        if (section.getSectionType() != SectionType.CONTENT) {
            throw new BadRequestException("Content blocks are only available for sections of type CONTENT");
        }

        List<SectionContentBlockResponse> response = sectionContentBlockService.getActiveBySection(sectionId);

        return ResponseEntity.ok(
                ApiResponse.<List<SectionContentBlockResponse>>builder()
                        .success(true)
                        .message("Public active section content blocks fetched successfully")
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