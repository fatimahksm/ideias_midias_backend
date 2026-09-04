package com.ideiasmidias.dataimport;

import com.ideiasmidias.category.dto.SectionCategoryRequest;
import com.ideiasmidias.category.entity.SectionCategory;
import com.ideiasmidias.category.repository.SectionCategoryRepository;
import com.ideiasmidias.category.service.SectionCategoryService;
import com.ideiasmidias.common.enums.ContactMethodType;
import com.ideiasmidias.common.enums.ContentBlockType;
import com.ideiasmidias.common.enums.SectionType;
import com.ideiasmidias.contact.dto.ContactMethodRequest;
import com.ideiasmidias.contact.service.ContactMethodService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideiasmidias.contentblock.dto.SectionContentBlockRequest;
import com.ideiasmidias.contentblock.service.SectionContentBlockService;
import com.ideiasmidias.dataimport.dto.FieldOverride;
import com.ideiasmidias.dataimport.dto.ImportFieldMeta;
import com.ideiasmidias.dataimport.dto.ImportFieldOption;
import com.ideiasmidias.dataimport.dto.ImportRowError;
import com.ideiasmidias.dataimport.dto.ImportRowSummary;
import com.ideiasmidias.dataimport.dto.ImportSheetResult;
import com.ideiasmidias.dataimport.dto.ImportSummaryResponse;
import com.ideiasmidias.homecard.dto.HomeCardRequest;
import com.ideiasmidias.homecard.service.HomeCardService;
import com.ideiasmidias.item.dto.SectionItemRequest;
import com.ideiasmidias.item.service.SectionItemService;
import com.ideiasmidias.portfolio.dto.PortfolioProjectRequest;
import com.ideiasmidias.portfolio.service.PortfolioProjectService;
import com.ideiasmidias.section.dto.SectionRequest;
import com.ideiasmidias.section.entity.Section;
import com.ideiasmidias.section.repository.SectionRepository;
import com.ideiasmidias.section.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads a multi-sheet .xlsx workbook covering the whole content model
 * (Sections, Categories, Items, PortfolioProjects, ContentBlocks, HomeCards,
 * ContactMethods) and either validates it (preview) or persists it (commit).
 *
 * <p>Every entity is created through its existing admin Service so the
 * import inherits the same business rules as the admin screens (slug
 * uniqueness, section-type compatibility, field limits, ...) instead of
 * duplicating them here.
 *
 * <p>Sheets are processed in dependency order. Foreign keys are written as
 * human-readable references (a section's name_en, a category name) rather than
 * raw ids, and are resolved against a registry that starts out pre-loaded
 * with everything already in the database and is extended as each sheet's
 * rows are created — so a single workbook can reference a section created a
 * few rows above it in the very same Sections sheet.
 *
 * <p>Every column's effective value (the Excel cell, or a {@link FieldOverride}
 * the admin already applied from the preview UI) is echoed back on
 * {@link ImportRowSummary#fields()}, and every sheet carries its own column
 * schema ({@link ImportSheetResult#fieldsMeta()}) and, for the fields that
 * must reference another row (a section's name_en, a category name) or a fixed
 * enum, the valid choices ({@link ImportSheetResult#fieldOptions()}) — so the
 * admin UI can render a fully editable, dropdown-backed table instead of
 * sending the admin back to the spreadsheet to fix a typo.
 */
@Service
@RequiredArgsConstructor
public class ExcelImportServiceImpl implements ExcelImportService {

    private final SectionRepository sectionRepository;
    private final SectionCategoryRepository sectionCategoryRepository;

    private final SectionService sectionService;
    private final SectionCategoryService sectionCategoryService;
    private final SectionItemService sectionItemService;
    private final PortfolioProjectService portfolioProjectService;
    private final SectionContentBlockService sectionContentBlockService;
    private final HomeCardService homeCardService;
    private final ContactMethodService contactMethodService;

    private final DataFormatter dataFormatter = new DataFormatter();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ---------------------------------------------------------- field schema

    private static final List<ImportFieldOption> SECTION_TYPE_OPTIONS = enumOptions(SectionType.values());
    private static final List<ImportFieldOption> CONTENT_BLOCK_TYPE_OPTIONS = enumOptions(ContentBlockType.values());
    private static final List<ImportFieldOption> CONTACT_METHOD_TYPE_OPTIONS = enumOptions(ContactMethodType.values());

    private static List<ImportFieldOption> enumOptions(Enum<?>[] values) {
        return Arrays.stream(values).map(v -> new ImportFieldOption(v.name(), v.name())).toList();
    }

    private static final List<ImportFieldMeta> SECTIONS_META = List.of(
            new ImportFieldMeta("name_pt", "TEXT", true),
            new ImportFieldMeta("name_en", "TEXT", true),
            new ImportFieldMeta("section_type", "SELECT", true),
            new ImportFieldMeta("description_pt", "TEXT", false),
            new ImportFieldMeta("description_en", "TEXT", false),
            new ImportFieldMeta("cover_image_url", "IMAGE", false),
            new ImportFieldMeta("cover_video_url", "VIDEO", false),
            new ImportFieldMeta("is_active", "BOOLEAN", false),
            new ImportFieldMeta("sort_order", "INTEGER", false),
            // Left blank, the backend derives a unique slug from name_en on its own.
            new ImportFieldMeta("slug", "TEXT", false)
    );

    private static final List<ImportFieldMeta> CATEGORIES_META = List.of(
            new ImportFieldMeta("section_name_en", "SELECT", true),
            new ImportFieldMeta("name_pt", "TEXT", true),
            new ImportFieldMeta("name_en", "TEXT", true),
            new ImportFieldMeta("description_pt", "TEXT", false),
            new ImportFieldMeta("description_en", "TEXT", false),
            new ImportFieldMeta("is_active", "BOOLEAN", false),
            new ImportFieldMeta("sort_order", "INTEGER", false)
    );

    private static final List<ImportFieldMeta> ITEMS_META = List.of(
            new ImportFieldMeta("section_name_en", "SELECT", true),
            new ImportFieldMeta("category_name_en", "SELECT", false),
            new ImportFieldMeta("title_pt", "TEXT", true),
            new ImportFieldMeta("title_en", "TEXT", true),
            new ImportFieldMeta("short_description_pt", "TEXT", false),
            new ImportFieldMeta("short_description_en", "TEXT", false),
            new ImportFieldMeta("full_description_pt", "TEXT", false),
            new ImportFieldMeta("full_description_en", "TEXT", false),
            new ImportFieldMeta("cover_image_url", "IMAGE", false),
            new ImportFieldMeta("video_url", "VIDEO", false),
            new ImportFieldMeta("item_type", "TEXT", false),
            new ImportFieldMeta("specifications_pt", "TEXT", false),
            new ImportFieldMeta("specifications_en", "TEXT", false),
            new ImportFieldMeta("is_featured", "BOOLEAN", false),
            new ImportFieldMeta("is_active", "BOOLEAN", false),
            new ImportFieldMeta("sort_order", "INTEGER", false)
    );

    private static final List<ImportFieldMeta> PORTFOLIO_PROJECTS_META = List.of(
            new ImportFieldMeta("section_name_en", "SELECT", true),
            new ImportFieldMeta("title_pt", "TEXT", true),
            new ImportFieldMeta("title_en", "TEXT", true),
            new ImportFieldMeta("short_description_pt", "TEXT", false),
            new ImportFieldMeta("short_description_en", "TEXT", false),
            new ImportFieldMeta("full_description_pt", "TEXT", false),
            new ImportFieldMeta("full_description_en", "TEXT", false),
            new ImportFieldMeta("client_name", "TEXT", false),
            new ImportFieldMeta("project_date", "DATE", false),
            new ImportFieldMeta("location_pt", "TEXT", false),
            new ImportFieldMeta("location_en", "TEXT", false),
            new ImportFieldMeta("cover_image_url", "IMAGE", false),
            new ImportFieldMeta("video_url", "VIDEO", false),
            new ImportFieldMeta("is_featured", "BOOLEAN", false),
            new ImportFieldMeta("is_active", "BOOLEAN", false),
            new ImportFieldMeta("sort_order", "INTEGER", false)
    );

    private static final List<ImportFieldMeta> CONTENT_BLOCKS_META = List.of(
            new ImportFieldMeta("section_name_en", "SELECT", true),
            new ImportFieldMeta("block_type", "SELECT", true),
            new ImportFieldMeta("title_pt", "TEXT", false),
            new ImportFieldMeta("title_en", "TEXT", false),
            new ImportFieldMeta("subtitle_pt", "TEXT", false),
            new ImportFieldMeta("subtitle_en", "TEXT", false),
            new ImportFieldMeta("content_pt", "TEXT", false),
            new ImportFieldMeta("content_en", "TEXT", false),
            new ImportFieldMeta("image_url", "IMAGE", false),
            new ImportFieldMeta("video_url", "VIDEO", false),
            new ImportFieldMeta("is_active", "BOOLEAN", false),
            new ImportFieldMeta("sort_order", "INTEGER", false)
    );

    private static final List<ImportFieldMeta> HOME_CARDS_META = List.of(
            new ImportFieldMeta("section_name_en", "SELECT", true),
            new ImportFieldMeta("title_pt", "TEXT", true),
            new ImportFieldMeta("title_en", "TEXT", true),
            new ImportFieldMeta("short_description_pt", "TEXT", false),
            new ImportFieldMeta("short_description_en", "TEXT", false),
            new ImportFieldMeta("image_url", "IMAGE", false),
            new ImportFieldMeta("icon_name", "TEXT", false),
            new ImportFieldMeta("is_active", "BOOLEAN", false),
            new ImportFieldMeta("sort_order", "INTEGER", false)
    );

    private static final List<ImportFieldMeta> CONTACT_METHODS_META = List.of(
            new ImportFieldMeta("type", "SELECT", true),
            new ImportFieldMeta("label_pt", "TEXT", false),
            new ImportFieldMeta("label_en", "TEXT", false),
            new ImportFieldMeta("value", "TEXT", true),
            new ImportFieldMeta("icon_name", "TEXT", false),
            new ImportFieldMeta("is_active", "BOOLEAN", false),
            new ImportFieldMeta("sort_order", "INTEGER", false)
    );

    // Every lookup below (sections, categories, and each sheet's own
    // resolution against them) reads lazy associations like
    // Category.getSection(), which needs a live Hibernate session for the
    // whole call — without a transaction here, each repository call opens
    // and closes its own, and the next lazy access blows up with
    // LazyInitializationException.
    @Override
    @Transactional(readOnly = true)
    public ImportSummaryResponse preview(MultipartFile file, String fieldOverridesJson) throws IOException {
        return run(file, true, fieldOverridesJson);
    }

    @Override
    @Transactional
    public ImportSummaryResponse commit(MultipartFile file, String fieldOverridesJson) throws IOException {
        return run(file, false, fieldOverridesJson);
    }

    private ImportSummaryResponse run(MultipartFile file, boolean dryRun, String fieldOverridesJson) throws IOException {
        Map<String, String> overrides = parseOverrides(fieldOverridesJson);

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            // Sections are referenced by name_en rather than slug: slug is now an
            // internal, backend-generated detail nobody has to type into a sheet.
            Map<String, Long> sectionIdByNameEn = new HashMap<>();
            List<ImportFieldOption> sectionOptions = new ArrayList<>();
            for (Section section : sectionRepository.findAll()) {
                sectionIdByNameEn.put(section.getNameEn().trim().toLowerCase(), section.getId());
                sectionOptions.add(new ImportFieldOption(section.getNameEn(), section.getNameEn()));
            }

            Map<String, Long> categoryIdByKey = new HashMap<>();
            List<ImportFieldOption> categoryOptions = new ArrayList<>();
            for (SectionCategory category : sectionCategoryRepository.findAll()) {
                categoryIdByKey.put(categoryKey(category.getSection().getId(), category.getNameEn()), category.getId());
                String sectionNameEn = category.getSection().getNameEn().trim().toLowerCase();
                categoryOptions.add(new ImportFieldOption(category.getNameEn(), category.getNameEn(), sectionNameEn));
            }

            List<ImportSheetResult> results = new ArrayList<>();
            results.add(processSections(workbook, dryRun, sectionIdByNameEn, overrides, sectionOptions));
            results.add(processCategories(workbook, dryRun, sectionIdByNameEn, categoryIdByKey, overrides, sectionOptions, categoryOptions));
            results.add(processItems(workbook, dryRun, sectionIdByNameEn, categoryIdByKey, overrides, sectionOptions, categoryOptions));
            results.add(processPortfolioProjects(workbook, dryRun, sectionIdByNameEn, overrides, sectionOptions));
            results.add(processContentBlocks(workbook, dryRun, sectionIdByNameEn, overrides, sectionOptions));
            results.add(processHomeCards(workbook, dryRun, sectionIdByNameEn, overrides, sectionOptions));
            results.add(processContactMethods(workbook, dryRun, overrides));

            return new ImportSummaryResponse(!dryRun, results);
        }
    }

    private Map<String, String> parseOverrides(String fieldOverridesJson) {
        if (fieldOverridesJson == null || fieldOverridesJson.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            List<FieldOverride> entries = objectMapper.readValue(
                    fieldOverridesJson, new TypeReference<List<FieldOverride>>() {
                    });
            Map<String, String> map = new HashMap<>();
            for (FieldOverride entry : entries) {
                if (entry.sheet() == null || entry.rowNumber() == null || entry.field() == null || entry.value() == null) {
                    continue;
                }
                map.put(overrideKey(entry.sheet(), entry.rowNumber(), entry.field()), entry.value());
            }
            return map;
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private String overrideKey(String sheet, int rowNumber, String field) {
        return sheet + "|" + rowNumber + "|" + field;
    }

    // ---------------------------------------------------------------- Sections

    private ImportSheetResult processSections(
            Workbook workbook, boolean dryRun,
            Map<String, Long> sectionIdByNameEn, Map<String, String> overrides,
            List<ImportFieldOption> sectionOptions
    ) {
        String sheetName = ImportSheetName.SECTIONS.sheetName();
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            return notPresent(sheetName, SECTIONS_META, Map.of("section_type", SECTION_TYPE_OPTIONS));
        }

        Map<String, Integer> h = headerIndex(sheet);
        List<ImportRowError> errors = new ArrayList<>();
        List<ImportRowSummary> rowSummaries = new ArrayList<>();
        int total = 0;
        int succeeded = 0;
        long placeholderId = -1;

        for (int r = sheet.getFirstRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (isBlankRow(row)) {
                continue;
            }
            total++;
            int excelRowNumber = r + 1;
            List<String> rowErrors = new ArrayList<>();

            String slug = effective(row, h, "slug", sheetName, excelRowNumber, overrides);
            String namePt = effective(row, h, "name_pt", sheetName, excelRowNumber, overrides);
            String nameEn = effective(row, h, "name_en", sheetName, excelRowNumber, overrides);
            String sectionTypeRaw = effective(row, h, "section_type", sheetName, excelRowNumber, overrides);
            SectionType sectionType = parseEnum(SectionType.class, sectionTypeRaw, "section_type", rowErrors);
            String descriptionPt = effective(row, h, "description_pt", sheetName, excelRowNumber, overrides);
            String descriptionEn = effective(row, h, "description_en", sheetName, excelRowNumber, overrides);
            String coverImageUrl = effective(row, h, "cover_image_url", sheetName, excelRowNumber, overrides);
            String coverVideoUrl = effective(row, h, "cover_video_url", sheetName, excelRowNumber, overrides);
            String isActiveRaw = effective(row, h, "is_active", sheetName, excelRowNumber, overrides);
            Boolean isActive = parseBool(isActiveRaw, "is_active", true, rowErrors);
            String sortOrderRaw = effective(row, h, "sort_order", sheetName, excelRowNumber, overrides);
            Integer sortOrder = parseInt(sortOrderRaw, "sort_order", 0, rowErrors);

            if (namePt == null) {
                rowErrors.add("name_pt is required");
            }
            if (nameEn == null) {
                rowErrors.add("name_en is required");
            }
            String normalizedSlug = slug == null ? null : slug.trim().toLowerCase();
            String normalizedNameEn = nameEn == null ? null : nameEn.trim().toLowerCase();
            if (normalizedNameEn != null && sectionIdByNameEn.containsKey(normalizedNameEn)) {
                rowErrors.add("a section named '" + nameEn + "' already exists");
            }

            Map<String, String> fields = new LinkedHashMap<>();
            fields.put("slug", nullToEmpty(slug));
            fields.put("name_pt", nullToEmpty(namePt));
            fields.put("name_en", nullToEmpty(nameEn));
            fields.put("section_type", nullToEmpty(sectionTypeRaw));
            fields.put("description_pt", nullToEmpty(descriptionPt));
            fields.put("description_en", nullToEmpty(descriptionEn));
            fields.put("cover_image_url", nullToEmpty(coverImageUrl));
            fields.put("cover_video_url", nullToEmpty(coverVideoUrl));
            fields.put("is_active", nullToEmpty(isActiveRaw));
            fields.put("sort_order", nullToEmpty(sortOrderRaw));
            rowSummaries.add(new ImportRowSummary(excelRowNumber, nameEn != null ? nameEn : slug, fields));

            if (!rowErrors.isEmpty()) {
                errors.add(new ImportRowError(excelRowNumber, String.join("; ", rowErrors)));
                continue;
            }

            SectionRequest request = new SectionRequest();
            request.setSlug(normalizedSlug);
            request.setNamePt(namePt);
            request.setNameEn(nameEn);
            request.setDescriptionPt(descriptionPt);
            request.setDescriptionEn(descriptionEn);
            request.setSectionType(sectionType);
            request.setCoverImageUrl(coverImageUrl);
            request.setCoverVideoUrl(coverVideoUrl);
            request.setIsActive(isActive);
            request.setSortOrder(sortOrder);

            try {
                Long id = dryRun ? placeholderId-- : sectionService.create(request).getId();
                sectionIdByNameEn.put(normalizedNameEn, id);
                sectionOptions.add(new ImportFieldOption(nameEn, nameEn));
                succeeded++;
            } catch (RuntimeException e) {
                errors.add(new ImportRowError(excelRowNumber, e.getMessage()));
            }
        }

        return new ImportSheetResult(sheetName, true, total, succeeded, errors, rowSummaries,
                SECTIONS_META, Map.of("section_type", SECTION_TYPE_OPTIONS));
    }

    // -------------------------------------------------------------- Categories

    private ImportSheetResult processCategories(
            Workbook workbook, boolean dryRun,
            Map<String, Long> sectionIdByNameEn, Map<String, Long> categoryIdByKey,
            Map<String, String> overrides,
            List<ImportFieldOption> sectionOptions, List<ImportFieldOption> categoryOptions
    ) {
        String sheetName = ImportSheetName.CATEGORIES.sheetName();
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            return notPresent(sheetName, CATEGORIES_META, Map.of("section_name_en", sectionOptions));
        }

        Map<String, Integer> h = headerIndex(sheet);
        List<ImportRowError> errors = new ArrayList<>();
        List<ImportRowSummary> rowSummaries = new ArrayList<>();
        int total = 0;
        int succeeded = 0;
        long placeholderId = -1;

        for (int r = sheet.getFirstRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (isBlankRow(row)) {
                continue;
            }
            total++;
            int excelRowNumber = r + 1;
            List<String> rowErrors = new ArrayList<>();

            String sectionNameEn = effective(row, h, "section_name_en", sheetName, excelRowNumber, overrides);
            String namePt = effective(row, h, "name_pt", sheetName, excelRowNumber, overrides);
            String nameEn = effective(row, h, "name_en", sheetName, excelRowNumber, overrides);
            String descriptionPt = effective(row, h, "description_pt", sheetName, excelRowNumber, overrides);
            String descriptionEn = effective(row, h, "description_en", sheetName, excelRowNumber, overrides);
            String isActiveRaw = effective(row, h, "is_active", sheetName, excelRowNumber, overrides);
            Boolean isActive = parseBool(isActiveRaw, "is_active", true, rowErrors);
            String sortOrderRaw = effective(row, h, "sort_order", sheetName, excelRowNumber, overrides);
            Integer sortOrder = parseInt(sortOrderRaw, "sort_order", 0, rowErrors);

            Long sectionId = resolveSectionId(sectionNameEn, sectionIdByNameEn, rowErrors);
            if (namePt == null) {
                rowErrors.add("name_pt is required");
            }
            if (nameEn == null) {
                rowErrors.add("name_en is required");
            }

            Map<String, String> fields = new LinkedHashMap<>();
            fields.put("section_name_en", nullToEmpty(sectionNameEn));
            fields.put("name_pt", nullToEmpty(namePt));
            fields.put("name_en", nullToEmpty(nameEn));
            fields.put("description_pt", nullToEmpty(descriptionPt));
            fields.put("description_en", nullToEmpty(descriptionEn));
            fields.put("is_active", nullToEmpty(isActiveRaw));
            fields.put("sort_order", nullToEmpty(sortOrderRaw));
            rowSummaries.add(new ImportRowSummary(excelRowNumber, nameEn != null ? nameEn : ("Row " + excelRowNumber), fields));

            if (!rowErrors.isEmpty()) {
                errors.add(new ImportRowError(excelRowNumber, String.join("; ", rowErrors)));
                continue;
            }

            SectionCategoryRequest request = new SectionCategoryRequest();
            request.setSectionId(sectionId);
            request.setNamePt(namePt);
            request.setNameEn(nameEn);
            request.setDescriptionPt(descriptionPt);
            request.setDescriptionEn(descriptionEn);
            request.setIsActive(isActive);
            request.setSortOrder(sortOrder);

            try {
                Long id = dryRun ? placeholderId-- : sectionCategoryService.create(request).getId();
                categoryIdByKey.put(categoryKey(sectionId, nameEn), id);
                categoryOptions.add(new ImportFieldOption(nameEn, nameEn, sectionNameEn.trim().toLowerCase()));
                succeeded++;
            } catch (RuntimeException e) {
                errors.add(new ImportRowError(excelRowNumber, e.getMessage()));
            }
        }

        return new ImportSheetResult(sheetName, true, total, succeeded, errors, rowSummaries,
                CATEGORIES_META, Map.of("section_name_en", sectionOptions));
    }

    // ------------------------------------------------------------------ Items

    private ImportSheetResult processItems(
            Workbook workbook, boolean dryRun,
            Map<String, Long> sectionIdByNameEn, Map<String, Long> categoryIdByKey, Map<String, String> overrides,
            List<ImportFieldOption> sectionOptions, List<ImportFieldOption> categoryOptions
    ) {
        String sheetName = ImportSheetName.ITEMS.sheetName();
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            return notPresent(sheetName, ITEMS_META,
                    Map.of("section_name_en", sectionOptions, "category_name_en", categoryOptions));
        }

        Map<String, Integer> h = headerIndex(sheet);
        List<ImportRowError> errors = new ArrayList<>();
        List<ImportRowSummary> rowSummaries = new ArrayList<>();
        int total = 0;
        int succeeded = 0;

        for (int r = sheet.getFirstRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (isBlankRow(row)) {
                continue;
            }
            total++;
            int excelRowNumber = r + 1;
            List<String> rowErrors = new ArrayList<>();

            String sectionNameEn = effective(row, h, "section_name_en", sheetName, excelRowNumber, overrides);
            String categoryNameEn = effective(row, h, "category_name_en", sheetName, excelRowNumber, overrides);
            String titlePt = effective(row, h, "title_pt", sheetName, excelRowNumber, overrides);
            String titleEn = effective(row, h, "title_en", sheetName, excelRowNumber, overrides);
            String shortDescriptionPt = effective(row, h, "short_description_pt", sheetName, excelRowNumber, overrides);
            String shortDescriptionEn = effective(row, h, "short_description_en", sheetName, excelRowNumber, overrides);
            String fullDescriptionPt = effective(row, h, "full_description_pt", sheetName, excelRowNumber, overrides);
            String fullDescriptionEn = effective(row, h, "full_description_en", sheetName, excelRowNumber, overrides);
            String coverImageUrl = effective(row, h, "cover_image_url", sheetName, excelRowNumber, overrides);
            String videoUrl = effective(row, h, "video_url", sheetName, excelRowNumber, overrides);
            String itemType = effective(row, h, "item_type", sheetName, excelRowNumber, overrides);
            String specificationsPt = effective(row, h, "specifications_pt", sheetName, excelRowNumber, overrides);
            String specificationsEn = effective(row, h, "specifications_en", sheetName, excelRowNumber, overrides);
            String isFeaturedRaw = effective(row, h, "is_featured", sheetName, excelRowNumber, overrides);
            Boolean isFeatured = parseBool(isFeaturedRaw, "is_featured", false, rowErrors);
            String isActiveRaw = effective(row, h, "is_active", sheetName, excelRowNumber, overrides);
            Boolean isActive = parseBool(isActiveRaw, "is_active", true, rowErrors);
            String sortOrderRaw = effective(row, h, "sort_order", sheetName, excelRowNumber, overrides);
            Integer sortOrder = parseInt(sortOrderRaw, "sort_order", 0, rowErrors);

            Long sectionId = resolveSectionId(sectionNameEn, sectionIdByNameEn, rowErrors);
            Long categoryId = resolveCategoryId(categoryNameEn, sectionId, categoryIdByKey, rowErrors);
            if (titlePt == null) {
                rowErrors.add("title_pt is required");
            }
            if (titleEn == null) {
                rowErrors.add("title_en is required");
            }

            Map<String, String> fields = new LinkedHashMap<>();
            fields.put("section_name_en", nullToEmpty(sectionNameEn));
            fields.put("category_name_en", nullToEmpty(categoryNameEn));
            fields.put("title_pt", nullToEmpty(titlePt));
            fields.put("title_en", nullToEmpty(titleEn));
            fields.put("short_description_pt", nullToEmpty(shortDescriptionPt));
            fields.put("short_description_en", nullToEmpty(shortDescriptionEn));
            fields.put("full_description_pt", nullToEmpty(fullDescriptionPt));
            fields.put("full_description_en", nullToEmpty(fullDescriptionEn));
            fields.put("cover_image_url", nullToEmpty(coverImageUrl));
            fields.put("video_url", nullToEmpty(videoUrl));
            fields.put("item_type", nullToEmpty(itemType));
            fields.put("specifications_pt", nullToEmpty(specificationsPt));
            fields.put("specifications_en", nullToEmpty(specificationsEn));
            fields.put("is_featured", nullToEmpty(isFeaturedRaw));
            fields.put("is_active", nullToEmpty(isActiveRaw));
            fields.put("sort_order", nullToEmpty(sortOrderRaw));
            rowSummaries.add(new ImportRowSummary(excelRowNumber, titleEn != null ? titleEn : titlePt, fields));

            if (!rowErrors.isEmpty()) {
                errors.add(new ImportRowError(excelRowNumber, String.join("; ", rowErrors)));
                continue;
            }

            SectionItemRequest request = new SectionItemRequest();
            request.setSectionId(sectionId);
            request.setCategoryId(categoryId);
            request.setTitlePt(titlePt);
            request.setTitleEn(titleEn);
            request.setShortDescriptionPt(shortDescriptionPt);
            request.setShortDescriptionEn(shortDescriptionEn);
            request.setFullDescriptionPt(fullDescriptionPt);
            request.setFullDescriptionEn(fullDescriptionEn);
            request.setCoverImageUrl(coverImageUrl);
            request.setVideoUrl(videoUrl);
            request.setItemType(itemType);
            request.setSpecificationsPt(specificationsPt);
            request.setSpecificationsEn(specificationsEn);
            request.setIsFeatured(isFeatured);
            request.setIsActive(isActive);
            request.setSortOrder(sortOrder);

            try {
                if (!dryRun) {
                    sectionItemService.create(request);
                }
                succeeded++;
            } catch (RuntimeException e) {
                errors.add(new ImportRowError(excelRowNumber, e.getMessage()));
            }
        }

        return new ImportSheetResult(sheetName, true, total, succeeded, errors, rowSummaries,
                ITEMS_META, Map.of("section_name_en", sectionOptions, "category_name_en", categoryOptions));
    }

    // -------------------------------------------------------- PortfolioProjects

    private ImportSheetResult processPortfolioProjects(
            Workbook workbook, boolean dryRun, Map<String, Long> sectionIdByNameEn, Map<String, String> overrides,
            List<ImportFieldOption> sectionOptions
    ) {
        String sheetName = ImportSheetName.PORTFOLIO_PROJECTS.sheetName();
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            return notPresent(sheetName, PORTFOLIO_PROJECTS_META, Map.of("section_name_en", sectionOptions));
        }

        Map<String, Integer> h = headerIndex(sheet);
        List<ImportRowError> errors = new ArrayList<>();
        List<ImportRowSummary> rowSummaries = new ArrayList<>();
        int total = 0;
        int succeeded = 0;

        for (int r = sheet.getFirstRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (isBlankRow(row)) {
                continue;
            }
            total++;
            int excelRowNumber = r + 1;
            List<String> rowErrors = new ArrayList<>();

            String sectionNameEn = effective(row, h, "section_name_en", sheetName, excelRowNumber, overrides);
            String titlePt = effective(row, h, "title_pt", sheetName, excelRowNumber, overrides);
            String titleEn = effective(row, h, "title_en", sheetName, excelRowNumber, overrides);
            String shortDescriptionPt = effective(row, h, "short_description_pt", sheetName, excelRowNumber, overrides);
            String shortDescriptionEn = effective(row, h, "short_description_en", sheetName, excelRowNumber, overrides);
            String fullDescriptionPt = effective(row, h, "full_description_pt", sheetName, excelRowNumber, overrides);
            String fullDescriptionEn = effective(row, h, "full_description_en", sheetName, excelRowNumber, overrides);
            String clientName = effective(row, h, "client_name", sheetName, excelRowNumber, overrides);
            String projectDateRaw = effective(row, h, "project_date", sheetName, excelRowNumber, overrides);
            LocalDate projectDate = parseDate(projectDateRaw, "project_date", rowErrors);
            String locationPt = effective(row, h, "location_pt", sheetName, excelRowNumber, overrides);
            String locationEn = effective(row, h, "location_en", sheetName, excelRowNumber, overrides);
            String coverImageUrl = effective(row, h, "cover_image_url", sheetName, excelRowNumber, overrides);
            String videoUrl = effective(row, h, "video_url", sheetName, excelRowNumber, overrides);
            String isFeaturedRaw = effective(row, h, "is_featured", sheetName, excelRowNumber, overrides);
            Boolean isFeatured = parseBool(isFeaturedRaw, "is_featured", false, rowErrors);
            String isActiveRaw = effective(row, h, "is_active", sheetName, excelRowNumber, overrides);
            Boolean isActive = parseBool(isActiveRaw, "is_active", true, rowErrors);
            String sortOrderRaw = effective(row, h, "sort_order", sheetName, excelRowNumber, overrides);
            Integer sortOrder = parseInt(sortOrderRaw, "sort_order", 0, rowErrors);

            Long sectionId = resolveSectionId(sectionNameEn, sectionIdByNameEn, rowErrors);
            if (titlePt == null) {
                rowErrors.add("title_pt is required");
            }
            if (titleEn == null) {
                rowErrors.add("title_en is required");
            }

            Map<String, String> fields = new LinkedHashMap<>();
            fields.put("section_name_en", nullToEmpty(sectionNameEn));
            fields.put("title_pt", nullToEmpty(titlePt));
            fields.put("title_en", nullToEmpty(titleEn));
            fields.put("short_description_pt", nullToEmpty(shortDescriptionPt));
            fields.put("short_description_en", nullToEmpty(shortDescriptionEn));
            fields.put("full_description_pt", nullToEmpty(fullDescriptionPt));
            fields.put("full_description_en", nullToEmpty(fullDescriptionEn));
            fields.put("client_name", nullToEmpty(clientName));
            fields.put("project_date", nullToEmpty(projectDateRaw));
            fields.put("location_pt", nullToEmpty(locationPt));
            fields.put("location_en", nullToEmpty(locationEn));
            fields.put("cover_image_url", nullToEmpty(coverImageUrl));
            fields.put("video_url", nullToEmpty(videoUrl));
            fields.put("is_featured", nullToEmpty(isFeaturedRaw));
            fields.put("is_active", nullToEmpty(isActiveRaw));
            fields.put("sort_order", nullToEmpty(sortOrderRaw));
            rowSummaries.add(new ImportRowSummary(excelRowNumber, titleEn != null ? titleEn : titlePt, fields));

            if (!rowErrors.isEmpty()) {
                errors.add(new ImportRowError(excelRowNumber, String.join("; ", rowErrors)));
                continue;
            }

            PortfolioProjectRequest request = new PortfolioProjectRequest();
            request.setSectionId(sectionId);
            request.setTitlePt(titlePt);
            request.setTitleEn(titleEn);
            request.setShortDescriptionPt(shortDescriptionPt);
            request.setShortDescriptionEn(shortDescriptionEn);
            request.setFullDescriptionPt(fullDescriptionPt);
            request.setFullDescriptionEn(fullDescriptionEn);
            request.setClientName(clientName);
            request.setProjectDate(projectDate);
            request.setLocationPt(locationPt);
            request.setLocationEn(locationEn);
            request.setCoverImageUrl(coverImageUrl);
            request.setVideoUrl(videoUrl);
            request.setIsFeatured(isFeatured);
            request.setIsActive(isActive);
            request.setSortOrder(sortOrder);

            try {
                if (!dryRun) {
                    portfolioProjectService.create(request);
                }
                succeeded++;
            } catch (RuntimeException e) {
                errors.add(new ImportRowError(excelRowNumber, e.getMessage()));
            }
        }

        return new ImportSheetResult(sheetName, true, total, succeeded, errors, rowSummaries,
                PORTFOLIO_PROJECTS_META, Map.of("section_name_en", sectionOptions));
    }

    // ----------------------------------------------------------- ContentBlocks

    private ImportSheetResult processContentBlocks(
            Workbook workbook, boolean dryRun, Map<String, Long> sectionIdByNameEn, Map<String, String> overrides,
            List<ImportFieldOption> sectionOptions
    ) {
        String sheetName = ImportSheetName.CONTENT_BLOCKS.sheetName();
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            return notPresent(sheetName, CONTENT_BLOCKS_META,
                    Map.of("section_name_en", sectionOptions, "block_type", CONTENT_BLOCK_TYPE_OPTIONS));
        }

        Map<String, Integer> h = headerIndex(sheet);
        List<ImportRowError> errors = new ArrayList<>();
        List<ImportRowSummary> rowSummaries = new ArrayList<>();
        int total = 0;
        int succeeded = 0;

        for (int r = sheet.getFirstRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (isBlankRow(row)) {
                continue;
            }
            total++;
            int excelRowNumber = r + 1;
            List<String> rowErrors = new ArrayList<>();

            String sectionNameEn = effective(row, h, "section_name_en", sheetName, excelRowNumber, overrides);
            String blockTypeRaw = effective(row, h, "block_type", sheetName, excelRowNumber, overrides);
            ContentBlockType blockType = parseEnum(ContentBlockType.class, blockTypeRaw, "block_type", rowErrors);
            String titlePt = effective(row, h, "title_pt", sheetName, excelRowNumber, overrides);
            String titleEn = effective(row, h, "title_en", sheetName, excelRowNumber, overrides);
            String subtitlePt = effective(row, h, "subtitle_pt", sheetName, excelRowNumber, overrides);
            String subtitleEn = effective(row, h, "subtitle_en", sheetName, excelRowNumber, overrides);
            String contentPt = effective(row, h, "content_pt", sheetName, excelRowNumber, overrides);
            String contentEn = effective(row, h, "content_en", sheetName, excelRowNumber, overrides);
            String imageUrl = effective(row, h, "image_url", sheetName, excelRowNumber, overrides);
            String videoUrl = effective(row, h, "video_url", sheetName, excelRowNumber, overrides);
            String isActiveRaw = effective(row, h, "is_active", sheetName, excelRowNumber, overrides);
            Boolean isActive = parseBool(isActiveRaw, "is_active", true, rowErrors);
            String sortOrderRaw = effective(row, h, "sort_order", sheetName, excelRowNumber, overrides);
            Integer sortOrder = parseInt(sortOrderRaw, "sort_order", 0, rowErrors);

            Long sectionId = resolveSectionId(sectionNameEn, sectionIdByNameEn, rowErrors);

            String label = titleEn != null ? titleEn
                    : (blockType != null ? blockType.name() + " (row " + excelRowNumber + ")" : "Row " + excelRowNumber);

            Map<String, String> fields = new LinkedHashMap<>();
            fields.put("section_name_en", nullToEmpty(sectionNameEn));
            fields.put("block_type", nullToEmpty(blockTypeRaw));
            fields.put("title_pt", nullToEmpty(titlePt));
            fields.put("title_en", nullToEmpty(titleEn));
            fields.put("subtitle_pt", nullToEmpty(subtitlePt));
            fields.put("subtitle_en", nullToEmpty(subtitleEn));
            fields.put("content_pt", nullToEmpty(contentPt));
            fields.put("content_en", nullToEmpty(contentEn));
            fields.put("image_url", nullToEmpty(imageUrl));
            fields.put("video_url", nullToEmpty(videoUrl));
            fields.put("is_active", nullToEmpty(isActiveRaw));
            fields.put("sort_order", nullToEmpty(sortOrderRaw));
            rowSummaries.add(new ImportRowSummary(excelRowNumber, label, fields));

            if (!rowErrors.isEmpty()) {
                errors.add(new ImportRowError(excelRowNumber, String.join("; ", rowErrors)));
                continue;
            }

            SectionContentBlockRequest request = new SectionContentBlockRequest();
            request.setSectionId(sectionId);
            request.setBlockType(blockType);
            request.setTitlePt(titlePt);
            request.setTitleEn(titleEn);
            request.setSubtitlePt(subtitlePt);
            request.setSubtitleEn(subtitleEn);
            request.setContentPt(contentPt);
            request.setContentEn(contentEn);
            request.setImageUrl(imageUrl);
            request.setVideoUrl(videoUrl);
            request.setIsActive(isActive);
            request.setSortOrder(sortOrder);

            try {
                if (!dryRun) {
                    sectionContentBlockService.create(request);
                }
                succeeded++;
            } catch (RuntimeException e) {
                errors.add(new ImportRowError(excelRowNumber, e.getMessage()));
            }
        }

        return new ImportSheetResult(sheetName, true, total, succeeded, errors, rowSummaries,
                CONTENT_BLOCKS_META, Map.of("section_name_en", sectionOptions, "block_type", CONTENT_BLOCK_TYPE_OPTIONS));
    }

    // --------------------------------------------------------------- HomeCards

    private ImportSheetResult processHomeCards(
            Workbook workbook, boolean dryRun, Map<String, Long> sectionIdByNameEn, Map<String, String> overrides,
            List<ImportFieldOption> sectionOptions
    ) {
        String sheetName = ImportSheetName.HOME_CARDS.sheetName();
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            return notPresent(sheetName, HOME_CARDS_META, Map.of("section_name_en", sectionOptions));
        }

        Map<String, Integer> h = headerIndex(sheet);
        List<ImportRowError> errors = new ArrayList<>();
        List<ImportRowSummary> rowSummaries = new ArrayList<>();
        int total = 0;
        int succeeded = 0;

        for (int r = sheet.getFirstRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (isBlankRow(row)) {
                continue;
            }
            total++;
            int excelRowNumber = r + 1;
            List<String> rowErrors = new ArrayList<>();

            String sectionNameEn = effective(row, h, "section_name_en", sheetName, excelRowNumber, overrides);
            String titlePt = effective(row, h, "title_pt", sheetName, excelRowNumber, overrides);
            String titleEn = effective(row, h, "title_en", sheetName, excelRowNumber, overrides);
            String shortDescriptionPt = effective(row, h, "short_description_pt", sheetName, excelRowNumber, overrides);
            String shortDescriptionEn = effective(row, h, "short_description_en", sheetName, excelRowNumber, overrides);
            String imageUrl = effective(row, h, "image_url", sheetName, excelRowNumber, overrides);
            String iconName = effective(row, h, "icon_name", sheetName, excelRowNumber, overrides);
            String isActiveRaw = effective(row, h, "is_active", sheetName, excelRowNumber, overrides);
            Boolean isActive = parseBool(isActiveRaw, "is_active", true, rowErrors);
            String sortOrderRaw = effective(row, h, "sort_order", sheetName, excelRowNumber, overrides);
            Integer sortOrder = parseInt(sortOrderRaw, "sort_order", 0, rowErrors);

            Long sectionId = resolveSectionId(sectionNameEn, sectionIdByNameEn, rowErrors);
            if (titlePt == null) {
                rowErrors.add("title_pt is required");
            }
            if (titleEn == null) {
                rowErrors.add("title_en is required");
            }

            Map<String, String> fields = new LinkedHashMap<>();
            fields.put("section_name_en", nullToEmpty(sectionNameEn));
            fields.put("title_pt", nullToEmpty(titlePt));
            fields.put("title_en", nullToEmpty(titleEn));
            fields.put("short_description_pt", nullToEmpty(shortDescriptionPt));
            fields.put("short_description_en", nullToEmpty(shortDescriptionEn));
            fields.put("image_url", nullToEmpty(imageUrl));
            fields.put("icon_name", nullToEmpty(iconName));
            fields.put("is_active", nullToEmpty(isActiveRaw));
            fields.put("sort_order", nullToEmpty(sortOrderRaw));
            rowSummaries.add(new ImportRowSummary(excelRowNumber, titleEn != null ? titleEn : titlePt, fields));

            if (!rowErrors.isEmpty()) {
                errors.add(new ImportRowError(excelRowNumber, String.join("; ", rowErrors)));
                continue;
            }

            HomeCardRequest request = new HomeCardRequest();
            request.setSectionId(sectionId);
            request.setTitlePt(titlePt);
            request.setTitleEn(titleEn);
            request.setShortDescriptionPt(shortDescriptionPt);
            request.setShortDescriptionEn(shortDescriptionEn);
            request.setImageUrl(imageUrl);
            request.setIconName(iconName);
            request.setIsActive(isActive);
            request.setSortOrder(sortOrder);

            try {
                if (!dryRun) {
                    homeCardService.create(request);
                }
                succeeded++;
            } catch (RuntimeException e) {
                errors.add(new ImportRowError(excelRowNumber, e.getMessage()));
            }
        }

        return new ImportSheetResult(sheetName, true, total, succeeded, errors, rowSummaries,
                HOME_CARDS_META, Map.of("section_name_en", sectionOptions));
    }

    // ---------------------------------------------------------- ContactMethods

    private ImportSheetResult processContactMethods(Workbook workbook, boolean dryRun, Map<String, String> overrides) {
        String sheetName = ImportSheetName.CONTACT_METHODS.sheetName();
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            return notPresent(sheetName, CONTACT_METHODS_META, Map.of("type", CONTACT_METHOD_TYPE_OPTIONS));
        }

        Map<String, Integer> h = headerIndex(sheet);
        List<ImportRowError> errors = new ArrayList<>();
        List<ImportRowSummary> rowSummaries = new ArrayList<>();
        int total = 0;
        int succeeded = 0;

        for (int r = sheet.getFirstRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (isBlankRow(row)) {
                continue;
            }
            total++;
            int excelRowNumber = r + 1;
            List<String> rowErrors = new ArrayList<>();

            String typeRaw = effective(row, h, "type", sheetName, excelRowNumber, overrides);
            ContactMethodType type = parseEnum(ContactMethodType.class, typeRaw, "type", rowErrors);
            String labelPt = effective(row, h, "label_pt", sheetName, excelRowNumber, overrides);
            String labelEn = effective(row, h, "label_en", sheetName, excelRowNumber, overrides);
            String value = effective(row, h, "value", sheetName, excelRowNumber, overrides);
            String iconName = effective(row, h, "icon_name", sheetName, excelRowNumber, overrides);
            String isActiveRaw = effective(row, h, "is_active", sheetName, excelRowNumber, overrides);
            Boolean isActive = parseBool(isActiveRaw, "is_active", true, rowErrors);
            String sortOrderRaw = effective(row, h, "sort_order", sheetName, excelRowNumber, overrides);
            Integer sortOrder = parseInt(sortOrderRaw, "sort_order", 0, rowErrors);

            if (value == null) {
                rowErrors.add("value is required");
            }

            Map<String, String> fields = new LinkedHashMap<>();
            fields.put("type", nullToEmpty(typeRaw));
            fields.put("label_pt", nullToEmpty(labelPt));
            fields.put("label_en", nullToEmpty(labelEn));
            fields.put("value", nullToEmpty(value));
            fields.put("icon_name", nullToEmpty(iconName));
            fields.put("is_active", nullToEmpty(isActiveRaw));
            fields.put("sort_order", nullToEmpty(sortOrderRaw));
            rowSummaries.add(new ImportRowSummary(excelRowNumber, value != null ? value : ("Row " + excelRowNumber), fields));

            if (!rowErrors.isEmpty()) {
                errors.add(new ImportRowError(excelRowNumber, String.join("; ", rowErrors)));
                continue;
            }

            ContactMethodRequest request = new ContactMethodRequest();
            request.setType(type);
            request.setLabelPt(labelPt);
            request.setLabelEn(labelEn);
            request.setValue(value);
            request.setIconName(iconName);
            request.setIsActive(isActive);
            request.setSortOrder(sortOrder);

            try {
                if (!dryRun) {
                    contactMethodService.create(request);
                }
                succeeded++;
            } catch (RuntimeException e) {
                errors.add(new ImportRowError(excelRowNumber, e.getMessage()));
            }
        }

        return new ImportSheetResult(sheetName, true, total, succeeded, errors, rowSummaries,
                CONTACT_METHODS_META, Map.of("type", CONTACT_METHOD_TYPE_OPTIONS));
    }

    // ------------------------------------------------------------------ shared

    private String categoryKey(Long sectionId, String nameEn) {
        return sectionId + "::" + nameEn.trim().toLowerCase();
    }

    private Long resolveSectionId(String nameEnRaw, Map<String, Long> sectionIdByNameEn, List<String> rowErrors) {
        if (nameEnRaw == null) {
            rowErrors.add("section_name_en is required");
            return null;
        }
        Long id = sectionIdByNameEn.get(nameEnRaw.trim().toLowerCase());
        if (id == null) {
            rowErrors.add("no section found named '" + nameEnRaw
                    + "' (add it to the Sections sheet, or use an existing section's name_en)");
        }
        return id;
    }

    private Long resolveCategoryId(String nameRaw, Long sectionId, Map<String, Long> categoryIdByKey, List<String> rowErrors) {
        if (nameRaw == null) {
            return null;
        }
        if (sectionId == null) {
            return null;
        }
        Long id = categoryIdByKey.get(categoryKey(sectionId, nameRaw));
        if (id == null) {
            rowErrors.add("no category named '" + nameRaw
                    + "' found in that section (add it to the Categories sheet, or use an existing category name)");
        }
        return id;
    }

    private ImportSheetResult notPresent(String sheetName, List<ImportFieldMeta> fieldsMeta, Map<String, List<ImportFieldOption>> fieldOptions) {
        return new ImportSheetResult(sheetName, false, 0, 0, List.of(), List.of(), fieldsMeta, fieldOptions);
    }

    private boolean isBlankRow(Row row) {
        if (row == null) {
            return true;
        }
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK
                    && !dataFormatter.formatCellValue(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private Map<String, Integer> headerIndex(Sheet sheet) {
        Map<String, Integer> map = new HashMap<>();
        Row header = sheet.getRow(sheet.getFirstRowNum());
        if (header == null) {
            return map;
        }
        for (Cell cell : header) {
            String key = dataFormatter.formatCellValue(cell).trim().toLowerCase().replace(' ', '_');
            if (!key.isEmpty()) {
                map.put(key, cell.getColumnIndex());
            }
        }
        return map;
    }

    private String cellStr(Row row, Integer col) {
        if (row == null || col == null) {
            return null;
        }
        Cell cell = row.getCell(col);
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC && org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate()
                    .toString();
        }
        String value = dataFormatter.formatCellValue(cell).trim();
        return value.isEmpty() ? null : value;
    }

    private String str(Row row, Map<String, Integer> h, String field) {
        return cellStr(row, h.get(field));
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    /**
     * Reads one column's effective value: an override the admin already
     * applied in the preview UI (an edited value, or a Gallery pick for an
     * image/video column) always wins over the Excel cell — including a
     * deliberately blank override, which clears the field the same as an
     * empty cell would. Absent from the overrides map at all means "no edit
     * yet", so the Excel cell's own text is used.
     */
    private String effective(
            Row row, Map<String, Integer> h, String field,
            String sheetName, int excelRowNumber, Map<String, String> overrides
    ) {
        String key = overrideKey(sheetName, excelRowNumber, field);
        if (overrides.containsKey(key)) {
            String value = overrides.get(key);
            return (value == null || value.isBlank()) ? null : value;
        }
        return str(row, h, field);
    }

    private Boolean parseBool(String raw, String field, Boolean defaultVal, List<String> rowErrors) {
        if (raw == null) {
            return defaultVal;
        }
        String v = raw.trim().toLowerCase();
        if (v.equals("true") || v.equals("yes") || v.equals("1") || v.equals("sim") || v.equals("x")) {
            return true;
        }
        if (v.equals("false") || v.equals("no") || v.equals("0") || v.equals("nao") || v.equals("não")) {
            return false;
        }
        rowErrors.add(field + " must be true/false");
        return defaultVal;
    }

    private Integer parseInt(String raw, String field, Integer defaultVal, List<String> rowErrors) {
        if (raw == null) {
            return defaultVal;
        }
        try {
            return (int) Double.parseDouble(raw.trim());
        } catch (NumberFormatException e) {
            rowErrors.add(field + " must be a whole number");
            return defaultVal;
        }
    }

    private LocalDate parseDate(String raw, String field, List<String> rowErrors) {
        if (raw == null) {
            return null;
        }
        try {
            return LocalDate.parse(raw.trim());
        } catch (DateTimeParseException e) {
            rowErrors.add(field + " must be a date in YYYY-MM-DD format");
            return null;
        }
    }

    private <E extends Enum<E>> E parseEnum(Class<E> type, String raw, String field, List<String> rowErrors) {
        if (raw == null) {
            rowErrors.add(field + " is required");
            return null;
        }
        try {
            return Enum.valueOf(type, raw.trim().toUpperCase().replace(' ', '_'));
        } catch (IllegalArgumentException e) {
            rowErrors.add(field + " must be one of "
                    + String.join(", ", Arrays.stream(type.getEnumConstants()).map(Enum::name).toList()));
            return null;
        }
    }
}
