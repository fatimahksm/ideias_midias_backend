package com.ideiasmidias.portfolio.dto;

import com.ideiasmidias.common.enums.MediaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortfolioProjectMediaRequest {

    @NotNull(message = "Project id is required")
    private Long projectId;

    @NotNull(message = "Media type is required")
    private MediaType mediaType;

    @NotBlank(message = "Media URL is required")
    private String mediaUrl;

    private String thumbnailUrl;

    @Size(max = 255, message = "Portuguese alt text must not exceed 255 characters")
    private String altTextPt;

    @Size(max = 255, message = "English alt text must not exceed 255 characters")
    private String altTextEn;

    private Boolean isActive = true;
    private Integer sortOrder = 0;
}