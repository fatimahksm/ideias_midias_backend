package com.ideiasmidias.contact.controller;

import com.ideiasmidias.common.enums.ContactMethodType;
import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.contact.dto.ContactMethodRequest;
import com.ideiasmidias.contact.dto.ContactMethodResponse;
import com.ideiasmidias.contact.service.ContactMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/contact-methods")
@RequiredArgsConstructor
public class ContactMethodController {

    private final ContactMethodService contactMethodService;

    @PostMapping
    public ResponseEntity<ApiResponse<ContactMethodResponse>> create(@Valid @RequestBody ContactMethodRequest request) {
        ContactMethodResponse response = contactMethodService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ContactMethodResponse>builder()
                        .success(true)
                        .message("Contact method created successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ContactMethodResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ContactMethodRequest request
    ) {
        ContactMethodResponse response = contactMethodService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.<ContactMethodResponse>builder()
                        .success(true)
                        .message("Contact method updated successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContactMethodResponse>> getById(@PathVariable Long id) {
        ContactMethodResponse response = contactMethodService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<ContactMethodResponse>builder()
                        .success(true)
                        .message("Contact method fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ContactMethodResponse>>> getAll() {
        List<ContactMethodResponse> response = contactMethodService.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<ContactMethodResponse>>builder()
                        .success(true)
                        .message("Contact methods fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ContactMethodResponse>>> getAllActive() {
        List<ContactMethodResponse> response = contactMethodService.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<ContactMethodResponse>>builder()
                        .success(true)
                        .message("Active contact methods fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<ContactMethodResponse>>> getByType(@PathVariable ContactMethodType type) {
        List<ContactMethodResponse> response = contactMethodService.getByType(type);

        return ResponseEntity.ok(
                ApiResponse.<List<ContactMethodResponse>>builder()
                        .success(true)
                        .message("Contact methods by type fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/type/{type}/active")
    public ResponseEntity<ApiResponse<List<ContactMethodResponse>>> getActiveByType(@PathVariable ContactMethodType type) {
        List<ContactMethodResponse> response = contactMethodService.getActiveByType(type);

        return ResponseEntity.ok(
                ApiResponse.<List<ContactMethodResponse>>builder()
                        .success(true)
                        .message("Active contact methods by type fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        contactMethodService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Contact method deleted successfully")
                        .data(null)
                        .build()
        );
    }
}