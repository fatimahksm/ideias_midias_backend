package com.ideiasmidias.dataimport.dto;

/**
 * Describes one column of a sheet so the admin UI can render the right kind
 * of editable control for it, in the order the columns should appear.
 *
 * <p>{@code type} is one of TEXT, BOOLEAN, INTEGER, DATE, SELECT, IMAGE,
 * VIDEO. SELECT means the valid values are listed in that sheet's
 * {@code fieldOptions} map under this field's name.
 */
public record ImportFieldMeta(String field, String type, boolean required) {
}
