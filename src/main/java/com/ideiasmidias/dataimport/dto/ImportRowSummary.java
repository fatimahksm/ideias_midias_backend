package com.ideiasmidias.dataimport.dto;

import java.util.Map;

/**
 * A data row's identity plus every column's effective value (the Excel
 * cell, or an override the admin already applied) — enough for the admin UI
 * to render a fully editable copy of the row, not just its image slots.
 */
public record ImportRowSummary(int rowNumber, String label, Map<String, String> fields) {
}
