package com.ideiasmidias.dataimport.dto;

import java.util.List;

public record ImportSummaryResponse(boolean committed, List<ImportSheetResult> sheets) {
}
