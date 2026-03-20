package com.ideiasmidias.media.service;

import com.ideiasmidias.common.enums.MediaType;
import com.ideiasmidias.common.exception.ResourceNotFoundException;
import com.ideiasmidias.media.dto.MediaLibraryResponse;
import com.ideiasmidias.media.entity.MediaLibrary;
import com.ideiasmidias.media.repository.MediaLibraryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MediaLibraryServiceImpl implements MediaLibraryService {

    private final MediaLibraryRepository mediaLibraryRepository;

    @Override
    @Transactional
    public MediaLibraryResponse getById(Long id) {
        return mapToResponse(getEntityById(id));
    }

    @Override
    @Transactional
    public List<MediaLibraryResponse> getAll() {
        return mediaLibraryRepository.findAllByOrderByIdDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<MediaLibraryResponse> getByType(MediaType fileType) {
        return mediaLibraryRepository.findAllByFileTypeOrderByIdDesc(fileType)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<MediaLibraryResponse> getByUploader(Long adminUserId) {
        return mediaLibraryRepository.findAllByUploadedBy_IdOrderByIdDesc(adminUserId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<MediaLibraryResponse> getByUploaderAndType(Long adminUserId, MediaType fileType) {
        return mediaLibraryRepository.findAllByUploadedBy_IdAndFileTypeOrderByIdDesc(adminUserId, fileType)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        MediaLibrary media = getEntityById(id);
        mediaLibraryRepository.delete(media);
    }

    private MediaLibrary getEntityById(Long id) {
        return mediaLibraryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Media library item not found with id: " + id));
    }

    private MediaLibraryResponse mapToResponse(MediaLibrary media) {
        return MediaLibraryResponse.builder()
                .id(media.getId())
                .fileName(media.getFileName())
                .originalName(media.getOriginalName())
                .fileUrl(media.getFileUrl())
                .fileType(media.getFileType())
                .mimeType(media.getMimeType())
                .fileSize(media.getFileSize())
                .uploadedById(media.getUploadedBy() != null ? media.getUploadedBy().getId() : null)
                .createdAt(media.getCreatedAt())
                .updatedAt(media.getUpdatedAt())
                .build();
    }
}