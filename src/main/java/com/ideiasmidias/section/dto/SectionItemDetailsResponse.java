package com.ideiasmidias.section.dto;

import com.ideiasmidias.item.dto.SectionItemMediaResponse;
import com.ideiasmidias.item.dto.SectionItemResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SectionItemDetailsResponse {

    private SectionItemResponse item;
    private List<SectionItemMediaResponse> media;
}
