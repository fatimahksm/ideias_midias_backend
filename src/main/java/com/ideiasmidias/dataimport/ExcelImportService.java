package com.ideiasmidias.dataimport;

import com.ideiasmidias.dataimport.dto.ImportSummaryResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ExcelImportService {

    /** Validates every row without persisting anything. */
    ImportSummaryResponse preview(MultipartFile file) throws IOException;

    /** Validates and persists every valid row, sheet by sheet, in dependency order. */
    ImportSummaryResponse commit(MultipartFile file) throws IOException;
}
