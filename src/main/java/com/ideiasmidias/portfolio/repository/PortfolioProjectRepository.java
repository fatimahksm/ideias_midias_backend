package com.ideiasmidias.portfolio.repository;

import com.ideiasmidias.portfolio.entity.PortfolioProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PortfolioProjectRepository extends JpaRepository<PortfolioProject, Long> {

    long countBySection_Id(Long sectionId);

    List<PortfolioProject> findAllByOrderBySortOrderAscIdAsc();

    List<PortfolioProject> findAllByIsActiveTrueOrderBySortOrderAscIdAsc();

    List<PortfolioProject> findAllBySection_IdOrderBySortOrderAscIdAsc(Long sectionId);

    List<PortfolioProject> findAllBySection_IdAndIsActiveTrueOrderBySortOrderAscIdAsc(Long sectionId);

    List<PortfolioProject> findAllBySection_IdAndIsFeaturedTrueOrderBySortOrderAscIdAsc(Long sectionId);

    List<PortfolioProject> findAllBySection_IdAndIsFeaturedTrueAndIsActiveTrueOrderBySortOrderAscIdAsc(Long sectionId);

    /**
     * Same idea as the items listing: the filters run in the database so the
     * screen can page through projects instead of downloading all of them.
     */
    @Query("""
            select p from PortfolioProject p
            where (:sectionId is null or p.section.id = :sectionId)
              and (:isActive is null or p.isActive = :isActive)
              and (:isFeatured is null or p.isFeatured = :isFeatured)
              and (cast(:search as string) is null
                   or lower(p.titleEn) like cast(:search as string)
                   or lower(p.titlePt) like cast(:search as string)
                   or lower(p.clientName) like cast(:search as string))
            """)
    Page<PortfolioProject> search(
            @Param("sectionId") Long sectionId,
            @Param("isActive") Boolean isActive,
            @Param("isFeatured") Boolean isFeatured,
            @Param("search") String search,
            Pageable pageable
    );

    // Counts behind the portfolio screen's stat cards, scoped like the listing.
    @Query("""
            select count(p) from PortfolioProject p
            where (:sectionId is null or p.section.id = :sectionId)
              and (:isActive is null or p.isActive = :isActive)
              and (:isFeatured is null or p.isFeatured = :isFeatured)
            """)
    long countScoped(
            @Param("sectionId") Long sectionId,
            @Param("isActive") Boolean isActive,
            @Param("isFeatured") Boolean isFeatured
    );
}
