package com.ideiasmidias.dataimport.dto;

/**
 * One image/video picked from the Gallery for a specific cell, sent by the
 * frontend after preview so an Excel-import row can carry a real media URL
 * without the person filling in the spreadsheet ever having to know or type
 * one — Excel cells hold text, not files, so this is how a picture actually
 * gets attached to a row.
 */
public record ImageOverrideEntry(String sheet, Integer rowNumber, String field, String url) {
}
