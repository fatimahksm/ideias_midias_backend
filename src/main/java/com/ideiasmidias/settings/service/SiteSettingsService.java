package com.ideiasmidias.settings.service;

import com.ideiasmidias.settings.dto.SiteSettingsRequest;
import com.ideiasmidias.settings.dto.SiteSettingsResponse;

public interface SiteSettingsService {

    SiteSettingsResponse getSiteSettings();

    SiteSettingsResponse saveOrUpdate(SiteSettingsRequest request);
}