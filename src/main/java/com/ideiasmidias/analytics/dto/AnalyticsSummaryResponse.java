package com.ideiasmidias.analytics.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AnalyticsSummaryResponse {

    private long viewsToday;
    private long viewsInRange;
    private long viewsAllTime;
    private long uniqueVisitorsToday;
    private long uniqueVisitorsInRange;
    private int rangeDays;
    private List<DailyPoint> dailySeries;
    private List<TopSection> topSections;

    @Getter
    @Setter
    @Builder
    public static class DailyPoint {
        private String date;
        private long views;
        private long uniqueVisitors;
    }

    @Getter
    @Setter
    @Builder
    public static class TopSection {
        private String slug;
        private String nameEn;
        private String namePt;
        private long views;
    }
}
