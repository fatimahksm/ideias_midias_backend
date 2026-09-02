package com.ideiasmidias.analytics.service;

import com.ideiasmidias.analytics.dto.AnalyticsSummaryResponse;
import com.ideiasmidias.analytics.repository.PageViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private static final int TREND_DAYS = 30;
    private static final int TOP_SECTIONS_LIMIT = 5;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private final PageViewRepository pageViewRepository;

    public AnalyticsSummaryResponse getSummary() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime trendStart = startOfToday.minusDays(TREND_DAYS - 1L);
        LocalDateTime epoch = LocalDateTime.of(2000, 1, 1, 0, 0);

        return AnalyticsSummaryResponse.builder()
                .viewsToday(pageViewRepository.countByViewedAtBetween(startOfToday, now))
                .viewsThisMonth(pageViewRepository.countByViewedAtBetween(startOfMonth, now))
                .viewsAllTime(pageViewRepository.countByViewedAtBetween(epoch, now))
                .uniqueVisitorsToday(pageViewRepository.countDistinctVisitorsBetween(startOfToday, now))
                .uniqueVisitorsThisMonth(pageViewRepository.countDistinctVisitorsBetween(startOfMonth, now))
                .dailySeries(buildDailySeries(trendStart))
                .topSections(buildTopSections(startOfMonth))
                .build();
    }

    private List<AnalyticsSummaryResponse.DailyPoint> buildDailySeries(LocalDateTime start) {
        List<Object[]> rows = pageViewRepository.findDailySeriesSince(start);

        return rows.stream()
                .map(row -> AnalyticsSummaryResponse.DailyPoint.builder()
                        .date(toLocalDate(row[0]).format(DATE_FORMAT))
                        .views(((Number) row[1]).longValue())
                        .uniqueVisitors(((Number) row[2]).longValue())
                        .build())
                .toList();
    }

    private List<AnalyticsSummaryResponse.TopSection> buildTopSections(LocalDateTime start) {
        List<Object[]> rows = pageViewRepository.findTopSectionsSince(start, TOP_SECTIONS_LIMIT);

        return rows.stream()
                .map(row -> AnalyticsSummaryResponse.TopSection.builder()
                        .slug((String) row[0])
                        .nameEn((String) row[1])
                        .namePt((String) row[2])
                        .views(((Number) row[3]).longValue())
                        .build())
                .toList();
    }

    private LocalDate toLocalDate(Object value) {
        if (value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime().toLocalDate();
        }

        if (value instanceof java.sql.Date date) {
            return date.toLocalDate();
        }

        if (value instanceof LocalDateTime dateTime) {
            return dateTime.toLocalDate();
        }

        if (value instanceof LocalDate date) {
            return date;
        }

        return LocalDate.parse(value.toString());
    }
}
