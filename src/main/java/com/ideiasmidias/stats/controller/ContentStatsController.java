package com.ideiasmidias.stats.controller;

import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.stats.dto.ContentStatsResponse;
import com.ideiasmidias.stats.service.ContentStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class ContentStatsController {

    private final ContentStatsService contentStatsService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<ContentStatsResponse>> getSummary() {
        return ResponseEntity.ok(
                ApiResponse.<ContentStatsResponse>builder()
                        .success(true)
                        .message("Content stats fetched successfully")
                        .data(contentStatsService.getSummary())
                        .build()
        );
    }
}
