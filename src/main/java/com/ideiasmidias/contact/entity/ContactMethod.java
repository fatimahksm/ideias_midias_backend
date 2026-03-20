package com.ideiasmidias.contact.entity;

import com.ideiasmidias.common.entity.BaseActiveSortableEntity;
import com.ideiasmidias.common.enums.ContactMethodType;
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
@Table(name = "contact_methods")
public class ContactMethod extends BaseActiveSortableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private ContactMethodType type;

    @Column(name = "label_pt", length = 150)
    private String labelPt;

    @Column(name = "label_en", length = 150)
    private String labelEn;

    @Column(name = "value", nullable = false, length = 500)
    private String value;

    @Column(name = "icon_name", length = 100)
    private String iconName;
}