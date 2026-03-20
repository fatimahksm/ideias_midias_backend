package com.ideiasmidias.contentblock.dto;

import com.ideiasmidias.common.enums.ContentBlockType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectionContentBlockRequest {

    @NotNull(message = "Section id is required")
    private Long sectionId;

    @NotNull(message = "Block type is required")
    private ContentBlockType blockType;

    @Size(max = 255, message = "Portuguese title must not exceed 255 characters")
    private String titlePt;

    @Size(max = 255, message = "English title must not exceed 255 characters")
    private String titleEn;

    @Size(max = 255, message = "Portuguese subtitle must not exceed 255 characters")
    private String subtitlePt;

    @Size(max = 255, message = "English subtitle must not exceed 255 characters")
    private String subtitleEn;

    private String contentPt;
    private String contentEn;
    private String imageUrl;
    private String videoUrl;

    private Boolean isActive = true;
    private Integer sortOrder = 0;
}