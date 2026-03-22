package com.ideiasmidias.publicapi.controller;

import com.ideiasmidias.common.enums.MediaType;
import com.ideiasmidias.common.enums.SectionType;
import com.ideiasmidias.common.exception.BadRequestException;
import com.ideiasmidias.common.exception.ResourceNotFoundException;
import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.item.dto.SectionItemMediaResponse;
import com.ideiasmidias.item.dto.SectionItemResponse;
import com.ideiasmidias.item.service.SectionItemMediaService;
import com.ideiasmidias.item.service.SectionItemService;
import com.ideiasmidias.section.dto.SectionResponse;
import com.ideiasmidias.section.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/items")
@RequiredArgsConstructor
public class PublicItemController {

    private final SectionItemService sectionItemService;
    private final SectionItemMediaService sectionItemMediaService;
    private final SectionService sectionService;

    @GetMapping("/{itemId}")
    public ResponseEntity<ApiResponse<SectionItemResponse>> getActiveItemById(
            @PathVariable Long itemId
    ) {
        SectionItemResponse response = getActivePublicItemOrThrow(itemId);

        return ResponseEntity.ok(
                ApiResponse.<SectionItemResponse>builder()
                        .success(true)
                        .message("Public active item fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{itemId}/media")
    public ResponseEntity<ApiResponse<List<SectionItemMediaResponse>>> getActiveItemMedia(
            @PathVariable Long itemId,
            @RequestParam(required = false) MediaType mediaType
    ) {
        SectionItemResponse item = getActivePublicItemOrThrow(itemId);

        List<SectionItemMediaResponse> response =
                mediaType == null
                        ? sectionItemMediaService.getActiveByItem(item.getId())
                        : sectionItemMediaService.getActiveByItemAndType(item.getId(), mediaType);

        return ResponseEntity.ok(
                ApiResponse.<List<SectionItemMediaResponse>>builder()
                        .success(true)
                        .message("Public active item media fetched successfully")
                        .data(response)
                        .build()
        );
    }

    private SectionItemResponse getActivePublicItemOrThrow(Long itemId) {
        SectionItemResponse item = sectionItemService.getById(itemId);

        if (!Boolean.TRUE.equals(item.getIsActive())) {
            throw new ResourceNotFoundException("Active item not found with id: " + itemId);
        }

        SectionResponse section = sectionService.getById(item.getSectionId());

        if (!Boolean.TRUE.equals(section.getIsActive())) {
            throw new ResourceNotFoundException("Active section not found for item id: " + itemId);
        }

        if (section.getSectionType() != SectionType.CATEGORY_ITEMS
                && section.getSectionType() != SectionType.DIRECT_ITEMS) {
            throw new BadRequestException(
                    "Items are only publicly available for sections of type CATEGORY_ITEMS or DIRECT_ITEMS"
            );
        }

        return item;
    }
}