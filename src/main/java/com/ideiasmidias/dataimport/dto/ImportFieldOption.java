package com.ideiasmidias.dataimport.dto;

/**
 * One valid choice for a dropdown-editable field (a section slug, a
 * category name, an enum value). {@code groupKey} is set only for options
 * that are scoped to another field's current value — a category only
 * belongs to one section, so its option carries that section's slug as
 * {@code groupKey} and the frontend filters the dropdown to the row's
 * current section_slug.
 */
public record ImportFieldOption(String value, String label, String groupKey) {

    public ImportFieldOption(String value, String label) {
        this(value, label, null);
    }
}
