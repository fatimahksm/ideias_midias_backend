package com.ideiasmidias.analytics.repository;

import com.ideiasmidias.analytics.entity.PageView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PageViewRepository extends JpaRepository<PageView, Long> {

    long countByViewedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("select count(distinct pv.visitorHash) from PageView pv where pv.viewedAt between :start and :end")
    long countDistinctVisitorsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(
        value = """
            select date_trunc('day', viewed_at) as day,
                   count(*) as views,
                   count(distinct visitor_hash) as unique_visitors
            from page_views
            where viewed_at >= :start
            group by day
            order by day
            """,
        nativeQuery = true
    )
    List<Object[]> findDailySeriesSince(@Param("start") LocalDateTime start);

    @Query(
        value = """
            select pv.section_slug as slug,
                   coalesce(s.name_en, pv.section_slug) as name_en,
                   coalesce(s.name_pt, pv.section_slug) as name_pt,
                   count(*) as views
            from page_views pv
            left join sections s on s.slug = pv.section_slug
            where pv.section_slug is not null and pv.viewed_at >= :start
            group by pv.section_slug, s.name_en, s.name_pt
            order by views desc
            limit :limit
            """,
        nativeQuery = true
    )
    List<Object[]> findTopSectionsSince(@Param("start") LocalDateTime start, @Param("limit") int limit);
}
