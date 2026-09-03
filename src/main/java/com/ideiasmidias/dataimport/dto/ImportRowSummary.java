package com.ideiasmidias.dataimport.dto;

import java.util.List;

/** A data row's identity plus its image/video slots, for sheets that have any. */
public record ImportRowSummary(int rowNumber, String label, List<ImportImageField> imageFields) {
}
