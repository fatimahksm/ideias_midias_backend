package com.ideiasmidias.analytics.controller;

import com.ideiasmidias.analytics.dto.AnalyticsSummaryResponse;
import com.ideiasmidias.analytics.service.AnalyticsService;
import com.ideiasmidias.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<AnalyticsSummaryResponse>> getSummary() {
        AnalyticsSummaryResponse response = analyticsService.getSummary();

        return ResponseEntity.ok(
                ApiResponse.<AnalyticsSummaryResponse>builder()
                        .success(true)
                        .message("Analytics summary fetched successfully")
                        .data(response)
                        .build()
        );
    }
}
