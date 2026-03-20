package com.ideiasmidias.item.service;

import com.ideiasmidias.common.enums.MediaType;
import com.ideiasmidias.item.dto.SectionItemMediaRequest;
import com.ideiasmidias.item.dto.SectionItemMediaResponse;

import java.util.List;

public interface SectionItemMediaService {

    SectionItemMediaResponse create(SectionItemMediaRequest request);

    SectionItemMediaResponse update(Long id, SectionItemMediaRequest request);

    SectionItemMediaResponse getById(Long id);

    List<SectionItemMediaResponse> getAll();

    List<SectionItemMediaResponse> getAllActive();

    List<SectionItemMediaResponse> getByItem(Long itemId);

    List<SectionItemMediaResponse> getActiveByItem(Long itemId);

    List<SectionItemMediaResponse> getByItemAndType(Long itemId, MediaType mediaType);

    List<SectionItemMediaResponse> getActiveByItemAndType(Long itemId, MediaType mediaType);

    void delete(Long id);
}