package com.ideiasmidias.dataimport.dto;

/**
 * One field the admin edited by hand after previewing an import, sent back
 * so the next preview/commit uses the edited value instead of whatever the
 * Excel cell held — including an image/video picked from the Gallery, since
 * a spreadsheet cell cannot hold an actual picture. A present entry always
 * wins over the cell, even when {@code value} is blank (that means the
 * admin deliberately cleared the field).
 */
public record FieldOverride(String sheet, Integer rowNumber, String field, String value) {
}
