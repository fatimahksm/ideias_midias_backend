package com.ideiasmidias.dataimport.dto;

import java.util.List;
import java.util.Map;

public record ImportSheetResult(
        String sheet,
        boolean present,
        int totalDataRows,
        int succeeded,
        List<ImportRowError> errors,
        List<ImportRowSummary> rows,
        List<ImportFieldMeta> fieldsMeta,
        Map<String, List<ImportFieldOption>> fieldOptions
) {
}
