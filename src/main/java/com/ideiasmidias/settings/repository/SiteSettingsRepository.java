package com.ideiasmidias.settings.repository;

import com.ideiasmidias.settings.entity.SiteSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SiteSettingsRepository extends JpaRepository<SiteSettings, Long> {

    Optional<SiteSettings> findTopByOrderByIdAsc();
}