package com.ideiasmidias.item.entity;

import com.ideiasmidias.category.entity.SectionCategory;
import com.ideiasmidias.common.entity.BaseActiveSortableEntity;
import com.ideiasmidias.section.entity.Section;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "section_items")
public class SectionItem extends BaseActiveSortableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private SectionCategory category;

    @Column(name = "title_pt", nullable = false, length = 255)
    private String titlePt;

    @Column(name = "title_en", nullable = false, length = 255)
    private String titleEn;

    @Column(name = "short_description_pt", columnDefinition = "TEXT")
    private String shortDescriptionPt;

    @Column(name = "short_description_en", columnDefinition = "TEXT")
    private String shortDescriptionEn;

    @Column(name = "full_description_pt", columnDefinition = "TEXT")
    private String fullDescriptionPt;

    @Column(name = "full_description_en", columnDefinition = "TEXT")
    private String fullDescriptionEn;

    @Column(name = "cover_image_url", columnDefinition = "TEXT")
    private String coverImageUrl;

    @Column(name = "video_url", columnDefinition = "TEXT")
    private String videoUrl;

    @Column(name = "item_type", length = 120)
    private String itemType;

    @Column(name = "specifications_pt", columnDefinition = "TEXT")
    private String specificationsPt;

    @Column(name = "specifications_en", columnDefinition = "TEXT")
    private String specificationsEn;

    @Column(name = "attributes_json", columnDefinition = "TEXT")
    private String attributesJson;

    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;
}
