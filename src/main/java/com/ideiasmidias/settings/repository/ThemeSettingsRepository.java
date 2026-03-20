package com.ideiasmidias.settings.repository;

import com.ideiasmidias.settings.entity.ThemeSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThemeSettingsRepository extends JpaRepository<ThemeSettings, Long> {

    Optional<ThemeSettings> findTopByOrderByIdAsc();
}