package com.ideiasmidias.settings.service;

import com.ideiasmidias.settings.dto.ThemeSettingsRequest;
import com.ideiasmidias.settings.dto.ThemeSettingsResponse;
import com.ideiasmidias.settings.entity.ThemeSettings;
import com.ideiasmidias.settings.repository.ThemeSettingsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ThemeSettingsServiceImpl implements ThemeSettingsService {

    private final ThemeSettingsRepository themeSettingsRepository;

    @Override
    public ThemeSettingsResponse getThemeSettings() {
        ThemeSettings themeSettings = themeSettingsRepository.findTopByOrderByIdAsc()
                .orElseGet(ThemeSettings::new);

        return mapToResponse(themeSettings);
    }

    @Override
    public ThemeSettingsResponse saveOrUpdate(ThemeSettingsRequest request) {
        ThemeSettings themeSettings = themeSettingsRepository.findTopByOrderByIdAsc()
                .orElseGet(ThemeSettings::new);

        applyRequestToEntity(themeSettings, request);

        ThemeSettings saved = themeSettingsRepository.save(themeSettings);
        return mapToResponse(saved);
    }

    private void applyRequestToEntity(ThemeSettings themeSettings, ThemeSettingsRequest request) {
        themeSettings.setPrimaryColor(request.getPrimaryColor());
        themeSettings.setSecondaryColor(request.getSecondaryColor());
        themeSettings.setAccentColor(request.getAccentColor());
        themeSettings.setBackgroundColor(request.getBackgroundColor());
        themeSettings.setTextColor(request.getTextColor());
        themeSettings.setHeroOverlayColor(request.getHeroOverlayColor());
    }

    private ThemeSettingsResponse mapToResponse(ThemeSettings themeSettings) {
        return ThemeSettingsResponse.builder()
                .id(themeSettings.getId())
                .primaryColor(themeSettings.getPrimaryColor())
                .secondaryColor(themeSettings.getSecondaryColor())
                .accentColor(themeSettings.getAccentColor())
                .backgroundColor(themeSettings.getBackgroundColor())
                .textColor(themeSettings.getTextColor())
                .heroOverlayColor(themeSettings.getHeroOverlayColor())
                .createdAt(themeSettings.getCreatedAt())
                .updatedAt(themeSettings.getUpdatedAt())
                .build();
    }
}