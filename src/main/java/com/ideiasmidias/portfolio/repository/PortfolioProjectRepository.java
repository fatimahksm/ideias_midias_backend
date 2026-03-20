package com.ideiasmidias.portfolio.repository;

import com.ideiasmidias.portfolio.entity.PortfolioProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioProjectRepository extends JpaRepository<PortfolioProject, Long> {

    List<PortfolioProject> findAllByOrderBySortOrderAscIdAsc();

    List<PortfolioProject> findAllByIsActiveTrueOrderBySortOrderAscIdAsc();

    List<PortfolioProject> findAllBySection_IdOrderBySortOrderAscIdAsc(Long sectionId);

    List<PortfolioProject> findAllBySection_IdAndIsActiveTrueOrderBySortOrderAscIdAsc(Long sectionId);

    List<PortfolioProject> findAllBySection_IdAndIsFeaturedTrueOrderBySortOrderAscIdAsc(Long sectionId);

    List<PortfolioProject> findAllBySection_IdAndIsFeaturedTrueAndIsActiveTrueOrderBySortOrderAscIdAsc(Long sectionId);
}