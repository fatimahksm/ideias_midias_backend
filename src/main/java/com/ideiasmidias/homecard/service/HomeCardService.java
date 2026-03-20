package com.ideiasmidias.homecard.service;

import com.ideiasmidias.homecard.dto.HomeCardRequest;
import com.ideiasmidias.homecard.dto.HomeCardResponse;

import java.util.List;

public interface HomeCardService {

    HomeCardResponse create(HomeCardRequest request);

    HomeCardResponse update(Long id, HomeCardRequest request);

    HomeCardResponse getById(Long id);

    List<HomeCardResponse> getAll();

    List<HomeCardResponse> getAllActive();

    List<HomeCardResponse> getBySection(Long sectionId);

    List<HomeCardResponse> getActiveBySection(Long sectionId);

    void delete(Long id);
}