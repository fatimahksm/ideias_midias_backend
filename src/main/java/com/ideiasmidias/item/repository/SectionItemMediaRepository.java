package com.ideiasmidias.item.repository;

import com.ideiasmidias.common.enums.MediaType;
import com.ideiasmidias.item.entity.SectionItemMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionItemMediaRepository extends JpaRepository<SectionItemMedia, Long> {

    long countByItem_Id(Long itemId);

    List<SectionItemMedia> findAllByOrderBySortOrderAscIdAsc();

    List<SectionItemMedia> findAllByIsActiveTrueOrderBySortOrderAscIdAsc();

    List<SectionItemMedia> findAllByItem_IdOrderBySortOrderAscIdAsc(Long itemId);

    List<SectionItemMedia> findAllByItem_IdAndIsActiveTrueOrderBySortOrderAscIdAsc(Long itemId);

    List<SectionItemMedia> findAllByItem_IdAndMediaTypeOrderBySortOrderAscIdAsc(Long itemId, MediaType mediaType);

    List<SectionItemMedia> findAllByItem_IdAndMediaTypeAndIsActiveTrueOrderBySortOrderAscIdAsc(Long itemId, MediaType mediaType);
}
