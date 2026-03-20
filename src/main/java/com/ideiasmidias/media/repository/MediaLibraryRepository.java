package com.ideiasmidias.media.repository;

import com.ideiasmidias.common.enums.MediaType;
import com.ideiasmidias.media.entity.MediaLibrary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaLibraryRepository extends JpaRepository<MediaLibrary, Long> {

    List<MediaLibrary> findAllByOrderByIdDesc();

    List<MediaLibrary> findAllByFileTypeOrderByIdDesc(MediaType fileType);

    List<MediaLibrary> findAllByUploadedBy_IdOrderByIdDesc(Long adminUserId);

    List<MediaLibrary> findAllByUploadedBy_IdAndFileTypeOrderByIdDesc(Long adminUserId, MediaType fileType);
}