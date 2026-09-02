package com.ideiasmidias.media.repository;

import com.ideiasmidias.common.enums.MediaType;
import com.ideiasmidias.media.entity.MediaLibrary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaLibraryRepository extends JpaRepository<MediaLibrary, Long> {

    List<MediaLibrary> findAllByOrderByIdDesc();

    List<MediaLibrary> findAllByFileTypeOrderByIdDesc(MediaType fileType);

    List<MediaLibrary> findAllByUploadedBy_IdOrderByIdDesc(Long adminUserId);

    List<MediaLibrary> findAllByUploadedBy_IdAndFileTypeOrderByIdDesc(Long adminUserId, MediaType fileType);

    // Paged variants. The library is the fastest-growing table, so the admin
    // screens read it a page at a time instead of pulling every row.

    Page<MediaLibrary> findAllByOrderByIdDesc(Pageable pageable);

    Page<MediaLibrary> findAllByFileTypeOrderByIdDesc(MediaType fileType, Pageable pageable);

    Page<MediaLibrary> findAllByUploadedBy_IdOrderByIdDesc(Long adminUserId, Pageable pageable);

    Page<MediaLibrary> findAllByUploadedBy_IdAndFileTypeOrderByIdDesc(
            Long adminUserId,
            MediaType fileType,
            Pageable pageable
    );
}