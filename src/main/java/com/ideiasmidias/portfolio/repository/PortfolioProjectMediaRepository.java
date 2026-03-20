package com.ideiasmidias.portfolio.repository;

import com.ideiasmidias.common.enums.MediaType;
import com.ideiasmidias.portfolio.entity.PortfolioProjectMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioProjectMediaRepository extends JpaRepository<PortfolioProjectMedia, Long> {

    List<PortfolioProjectMedia> findAllByOrderBySortOrderAscIdAsc();

    List<PortfolioProjectMedia> findAllByIsActiveTrueOrderBySortOrderAscIdAsc();

    List<PortfolioProjectMedia> findAllByProject_IdOrderBySortOrderAscIdAsc(Long projectId);

    List<PortfolioProjectMedia> findAllByProject_IdAndIsActiveTrueOrderBySortOrderAscIdAsc(Long projectId);

    List<PortfolioProjectMedia> findAllByProject_IdAndMediaTypeOrderBySortOrderAscIdAsc(Long projectId, MediaType mediaType);

    List<PortfolioProjectMedia> findAllByProject_IdAndMediaTypeAndIsActiveTrueOrderBySortOrderAscIdAsc(Long projectId, MediaType mediaType);
}