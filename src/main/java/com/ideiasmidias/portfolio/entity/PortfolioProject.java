package com.ideiasmidias.portfolio.entity;

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

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "portfolio_projects")
public class PortfolioProject extends BaseActiveSortableEntity {

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

    @Column(name = "full_description_pt", columnDefinition = "TEXT")
    private String fullDescriptionPt;

    @Column(name = "full_description_en", columnDefinition = "TEXT")
    private String fullDescriptionEn;

    @Column(name = "client_name", length = 255)
    private String clientName;

    @Column(name = "project_date")
    private LocalDate projectDate;

    @Column(name = "location_pt", length = 255)
    private String locationPt;

    @Column(name = "location_en", length = 255)
    private String locationEn;

    @Column(name = "cover_image_url", columnDefinition = "TEXT")
    private String coverImageUrl;

    @Column(name = "video_url", columnDefinition = "TEXT")
    private String videoUrl;

    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;
}