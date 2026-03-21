package com.ideiasmidias.settings.dto;

import com.ideiasmidias.common.enums.HeroBackgroundType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SiteSettingsRequest {

    @NotBlank(message = "Portuguese company name is required")
    @Size(max = 255, message = "Portuguese company name must not exceed 255 characters")
    private String companyNamePt;

    @NotBlank(message = "English company name is required")
    @Size(max = 255, message = "English company name must not exceed 255 characters")
    private String companyNameEn;

    private String shortIntroPt;
    private String shortIntroEn;

    @Size(max = 255, message = "Portuguese hero title must not exceed 255 characters")
    private String heroTitlePt;

    @Size(max = 255, message = "English hero title must not exceed 255 characters")
    private String heroTitleEn;

    private String heroSubtitlePt;
    private String heroSubtitleEn;

    private String logoUrl;

    @NotNull(message = "Hero background type is required")
    private HeroBackgroundType heroBackgroundType;

    private String heroBackgroundUrl;
    private String companyVideoUrl;
    private String addressPt;
    private String addressEn;
    private String mapEmbedUrl;

    @DecimalMin(value = "-90.0", inclusive = true, message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", inclusive = true, message = "Latitude must be <= 90")
    private BigDecimal locationLat;

    @DecimalMin(value = "-180.0", inclusive = true, message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", inclusive = true, message = "Longitude must be <= 180")
    private BigDecimal locationLng;
}