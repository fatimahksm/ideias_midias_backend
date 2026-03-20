package com.ideiasmidias.publicapi.controller;

import com.ideiasmidias.common.enums.SectionType;
import com.ideiasmidias.common.exception.BadRequestException;
import com.ideiasmidias.common.exception.ResourceNotFoundException;
import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.item.dto.SectionItemResponse;
import com.ideiasmidias.item.service.SectionItemService;
import com.ideiasmidias.section.dto.SectionResponse;
import com.ideiasmidias.section.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/sections")
@RequiredArgsConstructor
public class PublicSectionItemController {

    private final SectionService sectionService;
    private final SectionItemService sectionItemService;

    @GetMapping("/{sectionId}/items")
    public ResponseEntity<ApiResponse<List<SectionItemResponse>>> getActiveItemsBySection(
            @PathVariable Long sectionId
    ) {
        SectionResponse section = getActiveSectionOrThrow(sectionId);

        List<SectionItemResponse> response;

        if (section.getSectionType() == SectionType.CATEGORY_ITEMS) {
            response = sectionItemService.getActiveBySection(sectionId);
        } else if (section.getSectionType() == SectionType.DIRECT_ITEMS) {
            response = sectionItemService.getActiveDirectItemsBySection(sectionId);
        } else {
            throw new BadRequestException("Items are only available for sections of type CATEGORY_ITEMS or DIRECT_ITEMS");
        }

        return ResponseEntity.ok(
                ApiResponse.<List<SectionItemResponse>>builder()
                        .success(true)
                        .message("Public active section items fetched successfully")
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