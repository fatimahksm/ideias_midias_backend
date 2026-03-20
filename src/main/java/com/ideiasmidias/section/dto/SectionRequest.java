package com.ideiasmidias.section.dto;

import com.ideiasmidias.common.enums.SectionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectionRequest {

    @NotBlank(message = "Section slug is required")
    @Size(max = 180, message = "Section slug must not exceed 180 characters")
    private String slug;

    @NotBlank(message = "Portuguese section name is required")
    @Size(max = 255, message = "Portuguese section name must not exceed 255 characters")
    private String namePt;

    @NotBlank(message = "English section name is required")
    @Size(max = 255, message = "English section name must not exceed 255 characters")
    private String nameEn;

    private String descriptionPt;
    private String descriptionEn;

    @NotNull(message = "Section type is required")
    private SectionType sectionType;

    private String coverImageUrl;
    private String coverVideoUrl;

    private Boolean isActive = true;
    private Integer sortOrder = 0;
}