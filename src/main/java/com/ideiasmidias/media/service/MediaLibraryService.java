package com.ideiasmidias.media.service;

import com.ideiasmidias.common.enums.MediaType;
import com.ideiasmidias.media.dto.MediaLibraryResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MediaLibraryService {

    MediaLibraryResponse upload(MultipartFile file, Long uploadedById);

    MediaLibraryResponse getById(Long id);

    List<MediaLibraryResponse> getAll();

    List<MediaLibraryResponse> getByType(MediaType fileType);

    List<MediaLibraryResponse> getByUploader(Long adminUserId);

    List<MediaLibraryResponse> getByUploaderAndType(Long adminUserId, MediaType fileType);

    void delete(Long id);
}