package com.ideiasmidias.settings.dto;

import com.ideiasmidias.common.enums.HeroBackgroundType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SiteSettingsResponse {

    private Long id;
    private String companyNamePt;
    private String companyNameEn;
    private String shortIntroPt;
    private String shortIntroEn;
    private String heroTitlePt;
    private String heroTitleEn;
    private String heroSubtitlePt;
    private String heroSubtitleEn;
    private String logoUrl;
    private HeroBackgroundType heroBackgroundType;
    private String heroBackgroundUrl;
    private String companyVideoUrl;
    private String addressPt;
    private String addressEn;
    private String mapEmbedUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}