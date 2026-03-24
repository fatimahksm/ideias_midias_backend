package com.ideiasmidias.section.service;

import com.ideiasmidias.section.dto.SectionDetailsResponse;

public interface SectionDetailsService {

    SectionDetailsResponse getAdminDetailsById(Long sectionId);

    SectionDetailsResponse getPublicDetailsBySlug(String slug);
}
