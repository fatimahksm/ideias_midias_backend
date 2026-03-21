package com.ideiasmidias.settings.entity;

import com.ideiasmidias.common.entity.BaseEntity;
import com.ideiasmidias.common.enums.HeroBackgroundType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "site_settings")
public class SiteSettings extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name_pt", nullable = false, length = 255)
    private String companyNamePt;

    @Column(name = "company_name_en", nullable = false, length = 255)
    private String companyNameEn;

    @Column(name = "short_intro_pt", columnDefinition = "TEXT")
    private String shortIntroPt;

    @Column(name = "short_intro_en", columnDefinition = "TEXT")
    private String shortIntroEn;

    @Column(name = "hero_title_pt", length = 255)
    private String heroTitlePt;

    @Column(name = "hero_title_en", length = 255)
    private String heroTitleEn;

    @Column(name = "hero_subtitle_pt", columnDefinition = "TEXT")
    private String heroSubtitlePt;

    @Column(name = "hero_subtitle_en", columnDefinition = "TEXT")
    private String heroSubtitleEn;

    @Column(name = "logo_url", columnDefinition = "TEXT")
    private String logoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "hero_background_type", nullable = false, length = 50)
    private HeroBackgroundType heroBackgroundType = HeroBackgroundType.IMAGE;

    @Column(name = "hero_background_url", columnDefinition = "TEXT")
    private String heroBackgroundUrl;

    @Column(name = "company_video_url", columnDefinition = "TEXT")
    private String companyVideoUrl;

    @Column(name = "address_pt", columnDefinition = "TEXT")
    private String addressPt;

    @Column(name = "address_en", columnDefinition = "TEXT")
    private String addressEn;

    @Column(name = "map_embed_url", columnDefinition = "TEXT")
    private String mapEmbedUrl;

    @Column(name = "location_lat", precision = 10, scale = 7)
    private BigDecimal locationLat;

    @Column(name = "location_lng", precision = 10, scale = 7)
    private BigDecimal locationLng;
}