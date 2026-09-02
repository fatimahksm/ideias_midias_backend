package com.ideiasmidias.portfolio.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** Counts behind the portfolio screen's stat cards, scoped like the listing. */
@Getter
@Setter
@Builder
public class PortfolioProjectStatsResponse {

    private long total;
    private long active;
    private long featured;
}
