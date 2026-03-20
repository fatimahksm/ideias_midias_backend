package com.ideiasmidias.publicapi.controller;

import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.homecard.dto.HomeCardResponse;
import com.ideiasmidias.homecard.service.HomeCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/home-cards")
@RequiredArgsConstructor
public class PublicHomeCardController {

    private final HomeCardService homeCardService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<HomeCardResponse>>> getAllActive() {
        List<HomeCardResponse> response = homeCardService.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<HomeCardResponse>>builder()
                        .success(true)
                        .message("Public home cards fetched successfully")
                        .data(response)
                        .build()
        );
    }
}