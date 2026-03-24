package com.ideiasmidias.sectionattribute.entity;

import com.ideiasmidias.common.entity.BaseActiveSortableEntity;
import com.ideiasmidias.common.enums.AttributeFieldType;
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
@Table(name = "section_attribute_definitions")
public class SectionAttributeDefinition extends BaseActiveSortableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(name = "code", nullable = false, length = 120)
    private String code;

    @Column(name = "label_pt", nullable = false, length = 255)
    private String labelPt;

    @Column(name = "label_en", nullable = false, length = 255)
    private String labelEn;

    @Enumerated(EnumType.STRING)
    @Column(name = "field_type", nullable = false, length = 30)
    private AttributeFieldType fieldType;

    @Column(name = "is_required", nullable = false)
    private Boolean isRequired = false;

    @Column(name = "is_filterable", nullable = false)
    private Boolean isFilterable = false;

    @Column(name = "is_displayed_in_card", nullable = false)
    private Boolean isDisplayedInCard = true;

    @Column(name = "is_displayed_in_details", nullable = false)
    private Boolean isDisplayedInDetails = true;

    @Column(name = "options_json", columnDefinition = "TEXT")
    private String optionsJson;
}
