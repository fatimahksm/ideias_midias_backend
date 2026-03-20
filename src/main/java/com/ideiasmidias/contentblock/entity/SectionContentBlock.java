package com.ideiasmidias.contentblock.entity;

import com.ideiasmidias.common.entity.BaseActiveSortableEntity;
import com.ideiasmidias.common.enums.ContentBlockType;
import com.ideiasmidias.section.entity.Section;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "section_content_blocks")
public class SectionContentBlock extends BaseActiveSortableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Enumerated(EnumType.STRING)
    @Column(name = "block_type", nullable = false, length = 50)
    private ContentBlockType blockType;

    @Column(name = "title_pt", length = 255)
    private String titlePt;

    @Column(name = "title_en", length = 255)
    private String titleEn;

    @Column(name = "subtitle_pt", length = 255)
    private String subtitlePt;

    @Column(name = "subtitle_en", length = 255)
    private String subtitleEn;

    @Column(name = "content_pt", columnDefinition = "TEXT")
    private String contentPt;

    @Column(name = "content_en", columnDefinition = "TEXT")
    private String contentEn;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "video_url", columnDefinition = "TEXT")
    private String videoUrl;
}