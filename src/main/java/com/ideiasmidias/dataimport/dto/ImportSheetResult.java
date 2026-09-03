package com.ideiasmidias.dataimport.dto;

import java.util.List;

public record ImportSheetResult(
        String sheet,
        boolean present,
        int totalDataRows,
        int succeeded,
        List<ImportRowError> errors,
        List<ImportRowSummary> rows
) {
}
