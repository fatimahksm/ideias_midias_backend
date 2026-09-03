package com.ideiasmidias.dataimport;

import com.ideiasmidias.dataimport.dto.ImportSummaryResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ExcelImportService {

    /**
     * Validates every row without persisting anything.
     *
     * @param imageOverridesJson optional JSON array of {@code {sheet, rowNumber, field, url}}
     *                           entries for images/videos picked from the Gallery, which win
     *                           over whatever the Excel cell itself holds
     */
    ImportSummaryResponse preview(MultipartFile file, String imageOverridesJson) throws IOException;

    /** Validates and persists every valid row, sheet by sheet, in dependency order. */
    ImportSummaryResponse commit(MultipartFile file, String imageOverridesJson) throws IOException;
}
