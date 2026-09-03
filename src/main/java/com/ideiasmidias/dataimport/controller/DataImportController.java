package com.ideiasmidias.dataimport.controller;

import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.dataimport.ExcelImportService;
import com.ideiasmidias.dataimport.ExcelTemplateService;
import com.ideiasmidias.dataimport.dto.ImportSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin/data-import")
@RequiredArgsConstructor
public class DataImportController {

    private static final MediaType XLSX_MEDIA_TYPE =
            MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final ExcelImportService excelImportService;
    private final ExcelTemplateService excelTemplateService;

    @GetMapping("/template")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ByteArrayResource> downloadTemplate() throws IOException {
        byte[] bytes = excelTemplateService.buildTemplate();

        return ResponseEntity.ok()
                .contentType(XLSX_MEDIA_TYPE)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("import-template.xlsx").build().toString())
                .body(new ByteArrayResource(bytes));
    }

    @PostMapping(value = "/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<ImportSummaryResponse>> preview(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "imageOverrides", required = false) String imageOverrides
    ) throws IOException {
        ImportSummaryResponse response = excelImportService.preview(file, imageOverrides);

        return ResponseEntity.ok(
                ApiResponse.<ImportSummaryResponse>builder()
                        .success(true)
                        .message("Import file validated")
                        .data(response)
                        .build()
        );
    }

    @PostMapping(value = "/commit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<ImportSummaryResponse>> commit(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "imageOverrides", required = false) String imageOverrides
    ) throws IOException {
        ImportSummaryResponse response = excelImportService.commit(file, imageOverrides);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ImportSummaryResponse>builder()
                        .success(true)
                        .message("Import committed")
                        .data(response)
                        .build()
        );
    }
}
