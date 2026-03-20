package com.ideiasmidias.settings.service;

import com.ideiasmidias.settings.dto.ThemeSettingsRequest;
import com.ideiasmidias.settings.dto.ThemeSettingsResponse;

public interface ThemeSettingsService {

    ThemeSettingsResponse getThemeSettings();

    ThemeSettingsResponse saveOrUpdate(ThemeSettingsRequest request);
}