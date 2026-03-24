package com.ideiasmidias.section.entity;

import com.ideiasmidias.common.entity.BaseActiveSortableEntity;
import com.ideiasmidias.common.enums.SectionType;
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

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "sections")
public class Section extends BaseActiveSortableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slug", nullable = false, unique = true, length = 180)
    private String slug;

    @Column(name = "name_pt", nullable = false, length = 255)
    private String namePt;

    @Column(name = "name_en", nullable = false, length = 255)
    private String nameEn;

    @Column(name = "description_pt", columnDefinition = "TEXT")
    private String descriptionPt;

    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;

    @Enumerated(EnumType.STRING)
    @Column(name = "section_type", nullable = false, length = 50)
    private SectionType sectionType;

    @Column(name = "cover_image_url", columnDefinition = "TEXT")
    private String coverImageUrl;

    @Column(name = "cover_video_url", columnDefinition = "TEXT")
    private String coverVideoUrl;

    @Column(name = "display_variant", length = 100)
    private String displayVariant;

    @Column(name = "layout_style", length = 100)
    private String layoutStyle;

    @Column(name = "show_intro", nullable = false)
    private Boolean showIntro = true;

    @Column(name = "show_gallery", nullable = false)
    private Boolean showGallery = false;

    @Column(name = "show_filters", nullable = false)
    private Boolean showFilters = false;

    @Column(name = "show_item_details", nullable = false)
    private Boolean showItemDetails = true;

    @Column(name = "details_view_mode", length = 100)
    private String detailsViewMode;

    @Column(name = "allow_custom_attributes", nullable = false)
    private Boolean allowCustomAttributes = true;

    @Column(name = "settings_json", columnDefinition = "TEXT")
    private String settingsJson;
}
