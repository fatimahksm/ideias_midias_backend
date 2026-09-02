package com.ideiasmidias.stats.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** The counts the dashboard shows, so it never has to fetch whole tables. */
@Getter
@Setter
@Builder
public class ContentStatsResponse {

    private long sections;
    private long activeSections;
    private long categories;
    private long items;
    private long portfolioProjects;
    private long contentBlocks;
    private long mediaFiles;
    private long homeCards;
    private long contactMethods;
}
