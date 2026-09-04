package com.ideiasmidias.dataimport;

import com.ideiasmidias.dataimport.dto.ImportSummaryResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ExcelImportService {

    /**
     * Validates every row without persisting anything.
     *
     * @param fieldOverridesJson optional JSON array of {@code {sheet, rowNumber, field, value}}
     *                           entries — any field the admin edited in the preview UI (including
     *                           an image/video picked from the Gallery), which wins over whatever
     *                           the Excel cell itself holds
     */
    ImportSummaryResponse preview(MultipartFile file, String fieldOverridesJson) throws IOException;

    /** Validates and persists every valid row, sheet by sheet. */
    ImportSummaryResponse commit(MultipartFile file, String fieldOverridesJson) throws IOException;
}
