package com.ideiasmidias.section.dto;

import com.ideiasmidias.category.dto.SectionCategoryResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SectionCategoryDetailsResponse {

    private SectionCategoryResponse category;
    private List<SectionItemDetailsResponse> items;
}
