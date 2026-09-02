package com.ideiasmidias.publicapi.controller;

import com.ideiasmidias.analytics.dto.PageViewRequest;
import com.ideiasmidias.analytics.service.PageViewService;
import com.ideiasmidias.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The one place a public visit is recorded. The browser calls this once per
 * page it actually opens, so a view means "a person looked at this page" —
 * not "something fetched this data".
 */
@RestController
@RequestMapping("/api/public/analytics")
@RequiredArgsConstructor
public class PublicPageViewController {

    private final PageViewService pageViewService;

    @PostMapping("/page-views")
    public ResponseEntity<ApiResponse<Void>> recordPageView(
            @Valid @RequestBody PageViewRequest request,
            HttpServletRequest httpRequest
    ) {
        pageViewService.record(request, httpRequest);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Page view recorded")
                        .build()
        );
    }
}
