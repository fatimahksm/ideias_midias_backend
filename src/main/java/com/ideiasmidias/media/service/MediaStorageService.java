package com.ideiasmidias.media.service;

public interface MediaStorageService {

    StoredMediaFile store(org.springframework.web.multipart.MultipartFile file);

    void deleteByFileUrl(String fileUrl);
}