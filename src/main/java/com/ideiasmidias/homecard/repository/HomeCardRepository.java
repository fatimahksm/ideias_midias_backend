package com.ideiasmidias.homecard.repository;

import com.ideiasmidias.homecard.entity.HomeCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HomeCardRepository extends JpaRepository<HomeCard, Long> {

    List<HomeCard> findAllByOrderBySortOrderAscIdAsc();

    List<HomeCard> findAllByIsActiveTrueOrderBySortOrderAscIdAsc();

    List<HomeCard> findAllBySectionIdOrderBySortOrderAscIdAsc(Long sectionId);

    List<HomeCard> findAllBySectionIdAndIsActiveTrueOrderBySortOrderAscIdAsc(Long sectionId);
}