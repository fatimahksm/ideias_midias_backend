package com.ideiasmidias.settings.entity;

import com.ideiasmidias.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "theme_settings")
public class ThemeSettings extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "primary_color", nullable = false, length = 20)
    private String primaryColor;

    @Column(name = "secondary_color", nullable = false, length = 20)
    private String secondaryColor;

    @Column(name = "accent_color", nullable = false, length = 20)
    private String accentColor;

    @Column(name = "background_color", nullable = false, length = 20)
    private String backgroundColor;

    @Column(name = "text_color", nullable = false, length = 20)
    private String textColor;

    @Column(name = "hero_overlay_color", nullable = false, length = 20)
    private String heroOverlayColor;
}