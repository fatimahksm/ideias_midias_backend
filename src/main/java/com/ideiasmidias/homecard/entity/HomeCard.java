package com.ideiasmidias.homecard.entity;

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
@Table(name = "home_cards")
public class HomeCard extends BaseActiveSortableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(name = "title_pt", nullable = false, length = 255)
    private String titlePt;

    @Column(name = "title_en", nullable = false, length = 255)
    private String titleEn;

    @Column(name = "short_description_pt", columnDefinition = "TEXT")
    private String shortDescriptionPt;

    @Column(name = "short_description_en", columnDefinition = "TEXT")
    private String shortDescriptionEn;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "icon_name", length = 100)
    private String iconName;
}