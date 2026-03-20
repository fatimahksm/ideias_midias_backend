package com.ideiasmidias.media.service;

import com.ideiasmidias.common.enums.MediaType;
import com.ideiasmidias.media.dto.MediaLibraryResponse;

import java.util.List;

public interface MediaLibraryService {

    MediaLibraryResponse getById(Long id);

    List<MediaLibraryResponse> getAll();

    List<MediaLibraryResponse> getByType(MediaType fileType);

    List<MediaLibraryResponse> getByUploader(Long adminUserId);

    List<MediaLibraryResponse> getByUploaderAndType(Long adminUserId, MediaType fileType);

    void delete(Long id);
}