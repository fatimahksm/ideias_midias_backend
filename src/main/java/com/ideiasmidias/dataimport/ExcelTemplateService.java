package com.ideiasmidias.dataimport;

import com.ideiasmidias.common.enums.ContactMethodType;
import com.ideiasmidias.common.enums.ContentBlockType;
import com.ideiasmidias.common.enums.SectionType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/** Builds the downloadable, empty import template: one sheet per entity plus a legend. */
@Service
public class ExcelTemplateService {

    // Required columns are listed first in each array and marked with a
    // trailing "*" in the header cell (the parser strips it before matching).
    private static final Map<String, String[]> SHEET_COLUMNS = new LinkedHashMap<>();

    static {
        SHEET_COLUMNS.put(ImportSheetName.SECTIONS.sheetName(), new String[]{
                "slug*", "name_pt*", "name_en*", "section_type*",
                "description_pt", "description_en", "cover_image_url", "cover_video_url",
                "is_active", "sort_order"
        });
        SHEET_COLUMNS.put(ImportSheetName.CATEGORIES.sheetName(), new String[]{
                "section_slug*", "name_pt*", "name_en*",
                "description_pt", "description_en", "is_active", "sort_order"
        });
        SHEET_COLUMNS.put(ImportSheetName.ITEMS.sheetName(), new String[]{
                "section_slug*", "title_pt*", "title_en*",
                "category_name_en", "short_description_pt", "short_description_en",
                "full_description_pt", "full_description_en", "cover_image_url", "video_url",
                "item_type", "specifications_pt", "specifications_en",
                "is_featured", "is_active", "sort_order"
        });
        SHEET_COLUMNS.put(ImportSheetName.PORTFOLIO_PROJECTS.sheetName(), new String[]{
                "section_slug*", "title_pt*", "title_en*",
                "short_description_pt", "short_description_en", "full_description_pt", "full_description_en",
                "client_name", "project_date", "location_pt", "location_en",
                "cover_image_url", "video_url", "is_featured", "is_active", "sort_order"
        });
        SHEET_COLUMNS.put(ImportSheetName.CONTENT_BLOCKS.sheetName(), new String[]{
                "section_slug*", "block_type*",
                "title_pt", "title_en", "subtitle_pt", "subtitle_en", "content_pt", "content_en",
                "image_url", "video_url", "is_active", "sort_order"
        });
        SHEET_COLUMNS.put(ImportSheetName.HOME_CARDS.sheetName(), new String[]{
                "section_slug*", "title_pt*", "title_en*",
                "short_description_pt", "short_description_en", "image_url", "icon_name",
                "is_active", "sort_order"
        });
        SHEET_COLUMNS.put(ImportSheetName.CONTACT_METHODS.sheetName(), new String[]{
                "type*", "value*",
                "label_pt", "label_en", "icon_name", "is_active", "sort_order"
        });
    }

    public byte[] buildTemplate() throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle headerStyle = requiredHeaderStyle(workbook, true);
            CellStyle optionalHeaderStyle = requiredHeaderStyle(workbook, false);

            for (Map.Entry<String, String[]> entry : SHEET_COLUMNS.entrySet()) {
                Sheet sheet = workbook.createSheet(entry.getKey());
                Row header = sheet.createRow(0);
                String[] columns = entry.getValue();
                for (int i = 0; i < columns.length; i++) {
                    Cell cell = header.createCell(i);
                    boolean required = columns[i].endsWith("*");
                    cell.setCellValue(required ? columns[i].substring(0, columns[i].length() - 1) : columns[i]);
                    cell.setCellStyle(required ? headerStyle : optionalHeaderStyle);
                    sheet.setColumnWidth(i, 22 * 256);
                }
                sheet.createFreezePane(0, 1);
            }

            buildLegendSheet(workbook);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private void buildLegendSheet(XSSFWorkbook workbook) {
        Sheet sheet = workbook.createSheet("ReadMe");
        int rowNum = 0;

        rowNum = writeLegendBlock(sheet, rowNum,
                "How to use this file",
                new String[]{
                        "Fill in only the sheets you need — an empty or missing sheet is skipped.",
                        "Columns marked with * are required.",
                        "section_slug, category_name_en etc. must match a row from the Sections/Categories sheet",
                        "(either already in the site, or added earlier in this same file).",
                        "true/false columns also accept yes/no, sim/nao, 1/0.",
                        "Dates use the YYYY-MM-DD format, e.g. 2026-09-03.",
                        "Upload the file on the Import Data screen to preview it before committing."
                });
        rowNum++;
        rowNum = writeLegendBlock(sheet, rowNum, "What each column means",
                new String[]{
                        "slug / section_slug — the section's web address (letters, numbers, dashes), e.g. 'about-us'.",
                        "  A section_slug column must match a slug from the Sections sheet.",
                        "category_name_en — must match a name from the Categories sheet's name_en column.",
                        "  Leave it blank if the section has no categories.",
                        "name_pt/en, title_pt/en, label_pt/en — shown to visitors, in Portuguese / English.",
                        "description_*, short_description_*, full_description_*, specifications_*, content_* — text",
                        "  shown to visitors, in Portuguese / English. Optional.",
                        "section_type — what kind of section this is; see the values below.",
                        "block_type — what kind of content block this is; see the values below.",
                        "type (Contact Methods sheet) — what kind of contact method; see the values below.",
                        "cover_image_url, cover_video_url, image_url, video_url — leave these blank. After you upload",
                        "  the file, pick the actual picture or video from the Gallery on the preview screen.",
                        "is_active — true to make it visible on the live site, false to keep it hidden.",
                        "is_featured — true to highlight it, false for normal.",
                        "sort_order — a number; lower numbers appear first. Leave blank to add it at the end.",
                        "item_type — a free-text label, e.g. banner, sign, printing service. Optional.",
                        "icon_name — optional icon name, matched against the admin's icon picker.",
                        "value (Contact Methods sheet) — the actual phone number, email address, or link."
                });
        rowNum++;
        rowNum = writeLegendBlock(sheet, rowNum, "section_type values",
                enumNames(SectionType.values()));
        rowNum++;
        rowNum = writeLegendBlock(sheet, rowNum, "block_type values",
                enumNames(ContentBlockType.values()));
        rowNum++;
        writeLegendBlock(sheet, rowNum, "contact method type values",
                enumNames(ContactMethodType.values()));

        sheet.setColumnWidth(0, 100 * 256);
    }

    private String[] enumNames(Enum<?>[] values) {
        return Arrays.stream(values).map(Enum::name).collect(Collectors.toList()).toArray(new String[0]);
    }

    private int writeLegendBlock(Sheet sheet, int startRow, String title, String[] lines) {
        Row titleRow = sheet.createRow(startRow);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(title);
        Font boldFont = sheet.getWorkbook().createFont();
        boldFont.setBold(true);
        CellStyle boldStyle = sheet.getWorkbook().createCellStyle();
        boldStyle.setFont(boldFont);
        titleCell.setCellStyle(boldStyle);

        int rowNum = startRow + 1;
        for (String line : lines) {
            sheet.createRow(rowNum++).createCell(0).setCellValue(line);
        }
        return rowNum;
    }

    private CellStyle requiredHeaderStyle(XSSFWorkbook workbook, boolean required) {
        Font font = workbook.createFont();
        font.setBold(true);
        if (required) {
            font.setColor(IndexedColors.WHITE.getIndex());
        }
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        if (required) {
            style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            style.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        }
        return style;
    }
}
