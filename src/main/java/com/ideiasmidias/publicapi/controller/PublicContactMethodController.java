package com.ideiasmidias.publicapi.controller;

import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.contact.dto.ContactMethodResponse;
import com.ideiasmidias.contact.service.ContactMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/contact-methods")
@RequiredArgsConstructor
public class PublicContactMethodController {

    private final ContactMethodService contactMethodService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ContactMethodResponse>>> getAllActive() {
        List<ContactMethodResponse> response = contactMethodService.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<ContactMethodResponse>>builder()
                        .success(true)
                        .message("Public contact methods fetched successfully")
                        .data(response)
                        .build()
        );
    }
}