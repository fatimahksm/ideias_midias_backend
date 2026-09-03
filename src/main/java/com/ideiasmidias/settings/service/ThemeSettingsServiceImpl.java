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

    // Used until the owner saves a theme of their own. Before this, an
    // unsaved theme came back as an object of nulls, which the frontend
    // could only read as "unset every colour".
    private static final String DEFAULT_PRIMARY = "#0f172a";
    private static final String DEFAULT_SECONDARY = "#1e293b";
    private static final String DEFAULT_ACCENT = "#2563eb";
    private static final String DEFAULT_BACKGROUND = "#f8fafc";
    private static final String DEFAULT_TEXT = "#0f172a";
    private static final String DEFAULT_HERO_OVERLAY = "#0f172aa6";

    private final ThemeSettingsRepository themeSettingsRepository;

    @Override
    public ThemeSettingsResponse getThemeSettings() {
        return themeSettingsRepository.findTopByOrderByIdAsc()
                .map(this::mapToResponse)
                .orElseGet(ThemeSettingsServiceImpl::defaultResponse);
    }

    private static ThemeSettingsResponse defaultResponse() {
        return ThemeSettingsResponse.builder()
                .primaryColor(DEFAULT_PRIMARY)
                .secondaryColor(DEFAULT_SECONDARY)
                .accentColor(DEFAULT_ACCENT)
                .backgroundColor(DEFAULT_BACKGROUND)
                .textColor(DEFAULT_TEXT)
                .heroOverlayColor(DEFAULT_HERO_OVERLAY)
                .build();
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