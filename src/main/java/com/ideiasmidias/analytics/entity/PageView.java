package com.ideiasmidias.analytics.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * One recorded visit to a public page. Deliberately not a {@code BaseEntity}
 * (no updatedAt — a view is an immutable log line, not a mutable record).
 *
 * {@code visitorHash} is a one-way hash of the visitor's IP + user agent,
 * salted with the current date, so it can approximate "unique visitors per
 * day" without storing anything that identifies a real person or lets rows
 * be linked across days.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "page_views")
public class PageView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "path", nullable = false, length = 255)
    private String path;

    @Column(name = "section_slug", length = 180)
    private String sectionSlug;

    @Column(name = "visitor_hash", nullable = false, length = 64)
    private String visitorHash;

    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt;
}
