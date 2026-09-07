package com.ideiasmidias.settings.service;

import com.ideiasmidias.settings.dto.SiteSettingsRequest;
import com.ideiasmidias.settings.dto.SiteSettingsResponse;
import com.ideiasmidias.settings.entity.SiteSettings;
import com.ideiasmidias.settings.repository.SiteSettingsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class SiteSettingsServiceImpl implements SiteSettingsService {

    private final SiteSettingsRepository siteSettingsRepository;

    @Override
    public SiteSettingsResponse getSiteSettings() {
        SiteSettings siteSettings = siteSettingsRepository.findTopByOrderByIdAsc()
                .orElseGet(SiteSettings::new);

        return mapToResponse(siteSettings);
    }

    @Override
    public SiteSettingsResponse saveOrUpdate(SiteSettingsRequest request) {
        SiteSettings siteSettings = siteSettingsRepository.findTopByOrderByIdAsc()
                .orElseGet(SiteSettings::new);

        applyRequestToEntity(siteSettings, request);

        SiteSettings saved = siteSettingsRepository.save(siteSettings);
        return mapToResponse(saved);
    }

    private void applyRequestToEntity(SiteSettings siteSettings, SiteSettingsRequest request) {
        siteSettings.setCompanyNamePt(request.getCompanyNamePt());
        siteSettings.setCompanyNameEn(request.getCompanyNameEn());
        siteSettings.setShortIntroPt(request.getShortIntroPt());
        siteSettings.setShortIntroEn(request.getShortIntroEn());
        siteSettings.setHeroTitlePt(request.getHeroTitlePt());
        siteSettings.setHeroTitleEn(request.getHeroTitleEn());
        siteSettings.setHeroSubtitlePt(request.getHeroSubtitlePt());
        siteSettings.setHeroSubtitleEn(request.getHeroSubtitleEn());
        siteSettings.setLogoUrl(request.getLogoUrl());
        siteSettings.setHeroBackgroundType(request.getHeroBackgroundType());
        siteSettings.setHeroBackgroundUrl(request.getHeroBackgroundUrl());
        siteSettings.setCompanyVideoUrl(request.getCompanyVideoUrl());
        siteSettings.setAddressPt(request.getAddressPt());
        siteSettings.setAddressEn(request.getAddressEn());
        siteSettings.setMapEmbedUrl(request.getMapEmbedUrl());
        siteSettings.setLocationLat(request.getLocationLat());
        siteSettings.setLocationLng(request.getLocationLng());
        siteSettings.setAddress2Pt(request.getAddress2Pt());
        siteSettings.setAddress2En(request.getAddress2En());
        siteSettings.setMapEmbedUrl2(request.getMapEmbedUrl2());
        siteSettings.setLocation2Lat(request.getLocation2Lat());
        siteSettings.setLocation2Lng(request.getLocation2Lng());
    }

    private SiteSettingsResponse mapToResponse(SiteSettings siteSettings) {
        return SiteSettingsResponse.builder()
                .id(siteSettings.getId())
                .companyNamePt(siteSettings.getCompanyNamePt())
                .companyNameEn(siteSettings.getCompanyNameEn())
                .shortIntroPt(siteSettings.getShortIntroPt())
                .shortIntroEn(siteSettings.getShortIntroEn())
                .heroTitlePt(siteSettings.getHeroTitlePt())
                .heroTitleEn(siteSettings.getHeroTitleEn())
                .heroSubtitlePt(siteSettings.getHeroSubtitlePt())
                .heroSubtitleEn(siteSettings.getHeroSubtitleEn())
                .logoUrl(siteSettings.getLogoUrl())
                .heroBackgroundType(siteSettings.getHeroBackgroundType())
                .heroBackgroundUrl(siteSettings.getHeroBackgroundUrl())
                .companyVideoUrl(siteSettings.getCompanyVideoUrl())
                .addressPt(siteSettings.getAddressPt())
                .addressEn(siteSettings.getAddressEn())
                .mapEmbedUrl(siteSettings.getMapEmbedUrl())
                .locationLat(siteSettings.getLocationLat())
                .locationLng(siteSettings.getLocationLng())
                .address2Pt(siteSettings.getAddress2Pt())
                .address2En(siteSettings.getAddress2En())
                .mapEmbedUrl2(siteSettings.getMapEmbedUrl2())
                .location2Lat(siteSettings.getLocation2Lat())
                .location2Lng(siteSettings.getLocation2Lng())
                .createdAt(siteSettings.getCreatedAt())
                .updatedAt(siteSettings.getUpdatedAt())
                .build();
    }
}