package com.ideiasmidias.media.service;

import com.ideiasmidias.common.enums.MediaType;
import com.ideiasmidias.common.response.PageResponse;
import com.ideiasmidias.media.dto.MediaLibraryResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MediaLibraryService {

    MediaLibraryResponse upload(MultipartFile file, Long uploadedById);

    MediaLibraryResponse getById(Long id);

    List<MediaLibraryResponse> getAll();

    /**
     * One page of the library, newest first. {@code fileType} and
     * {@code uploaderId} are optional filters.
     */
    PageResponse<MediaLibraryResponse> getPage(MediaType fileType, Long uploaderId, int page, int size);

    List<MediaLibraryResponse> getByType(MediaType fileType);

    List<MediaLibraryResponse> getByUploader(Long adminUserId);

    List<MediaLibraryResponse> getByUploaderAndType(Long adminUserId, MediaType fileType);

    void delete(Long id);
}