package com.ideiasmidias.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** Counts behind the items screen's stat cards, scoped like the listing. */
@Getter
@Setter
@Builder
public class SectionItemStatsResponse {

    private long total;
    private long active;
    private long featured;
}
