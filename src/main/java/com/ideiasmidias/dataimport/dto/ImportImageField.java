package com.ideiasmidias.dataimport.dto;

/** One image/video slot on a row: which column it is, and its current value (Excel cell or a picked override). */
public record ImportImageField(String field, String mediaType, String currentValue) {
}
