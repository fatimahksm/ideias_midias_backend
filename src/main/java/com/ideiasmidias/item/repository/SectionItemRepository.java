package com.ideiasmidias.item.repository;

import com.ideiasmidias.item.entity.SectionItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SectionItemRepository extends JpaRepository<SectionItem, Long> {

    long countBySection_Id(Long sectionId);

    long countByCategory_Id(Long categoryId);

    List<SectionItem> findAllByOrderBySortOrderAscIdAsc();

    List<SectionItem> findAllByIsActiveTrueOrderBySortOrderAscIdAsc();

    List<SectionItem> findAllBySection_IdOrderBySortOrderAscIdAsc(Long sectionId);

    List<SectionItem> findAllBySection_IdAndIsActiveTrueOrderBySortOrderAscIdAsc(Long sectionId);

    List<SectionItem> findAllByCategory_IdOrderBySortOrderAscIdAsc(Long categoryId);

    List<SectionItem> findAllByCategory_IdAndIsActiveTrueOrderBySortOrderAscIdAsc(Long categoryId);

    List<SectionItem> findAllBySection_IdAndCategoryIsNullOrderBySortOrderAscIdAsc(Long sectionId);

    List<SectionItem> findAllBySection_IdAndCategoryIsNullAndIsActiveTrueOrderBySortOrderAscIdAsc(Long sectionId);

    List<SectionItem> findAllBySection_IdAndIsFeaturedTrueOrderBySortOrderAscIdAsc(Long sectionId);

    List<SectionItem> findAllBySection_IdAndIsFeaturedTrueAndIsActiveTrueOrderBySortOrderAscIdAsc(Long sectionId);

    /**
     * Every filter the items screens offer, applied in the database.
     *
     * <p>Filtering used to happen in the browser over the whole table, which
     * cannot be paged: a page of 24 rows would only ever search those 24. Each
     * parameter is optional — null means "do not filter on this".
     *
     * <p>{@code search} arrives as a ready lower-cased LIKE pattern, and is
     * cast explicitly: Postgres cannot infer the type of a null string
     * parameter and would otherwise reject the comparison as text ~~ bytea.
     */
    @Query("""
            select i from SectionItem i
            where (:sectionId is null or i.section.id = :sectionId)
              and (:categoryId is null or i.category.id = :categoryId)
              and (:onlyUncategorized = false or i.category is null)
              and (:isActive is null or i.isActive = :isActive)
              and (:isFeatured is null or i.isFeatured = :isFeatured)
              and (cast(:search as string) is null
                   or lower(i.titleEn) like cast(:search as string)
                   or lower(i.titlePt) like cast(:search as string))
            """)
    Page<SectionItem> search(
            @Param("sectionId") Long sectionId,
            @Param("categoryId") Long categoryId,
            @Param("onlyUncategorized") boolean onlyUncategorized,
            @Param("isActive") Boolean isActive,
            @Param("isFeatured") Boolean isFeatured,
            @Param("search") String search,
            Pageable pageable
    );

    // Counts for the items screen's stat cards, scoped the same way the
    // listing is. Reading them as counts keeps the cards honest once the
    // listing itself only holds one page.

    @Query("""
            select count(i) from SectionItem i
            where (:sectionId is null or i.section.id = :sectionId)
              and (:categoryId is null or i.category.id = :categoryId)
              and (:isActive is null or i.isActive = :isActive)
              and (:isFeatured is null or i.isFeatured = :isFeatured)
            """)
    long countScoped(
            @Param("sectionId") Long sectionId,
            @Param("categoryId") Long categoryId,
            @Param("isActive") Boolean isActive,
            @Param("isFeatured") Boolean isFeatured
    );
}
