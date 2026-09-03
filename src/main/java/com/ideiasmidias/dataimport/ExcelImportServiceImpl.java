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
import com.ideiasmidias.contentblock.dto.SectionContentBlockRequest;
import com.ideiasmidias.contentblock.service.SectionContentBlockService;
import com.ideiasmidias.dataimport.dto.ImportRowError;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
 * human-readable references (a section slug, a category name) rather than
 * raw ids, and are resolved against a registry that starts out pre-loaded
 * with everything already in the database and is extended as each sheet's
 * rows are created — so a single workbook can reference a section created a
 * few rows above it in the very same Sections sheet.
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

    @Override
    public ImportSummaryResponse preview(MultipartFile file) throws IOException {
        return run(file, true);
    }

    @Override
    public ImportSummaryResponse commit(MultipartFile file) throws IOException {
        return run(file, false);
    }

    private ImportSummaryResponse run(MultipartFile file, boolean dryRun) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Map<String, Long> sectionIdBySlug = new HashMap<>();
            for (Section section : sectionRepository.findAll()) {
                sectionIdBySlug.put(section.getSlug().trim().toLowerCase(), section.getId());
            }

            Map<String, Long> categoryIdByKey = new HashMap<>();
            for (SectionCategory category : sectionCategoryRepository.findAll()) {
                categoryIdByKey.put(categoryKey(category.getSection().getId(), category.getNameEn()), category.getId());
            }

            List<ImportSheetResult> results = new ArrayList<>();
            results.add(processSections(workbook, dryRun, sectionIdBySlug));
            results.add(processCategories(workbook, dryRun, sectionIdBySlug, categoryIdByKey));
            results.add(processItems(workbook, dryRun, sectionIdBySlug, categoryIdByKey));
            results.add(processPortfolioProjects(workbook, dryRun, sectionIdBySlug));
            results.add(processContentBlocks(workbook, dryRun, sectionIdBySlug));
            results.add(processHomeCards(workbook, dryRun, sectionIdBySlug));
            results.add(processContactMethods(workbook, dryRun));

            return new ImportSummaryResponse(!dryRun, results);
        }
    }

    // ---------------------------------------------------------------- Sections

    private ImportSheetResult processSections(Workbook workbook, boolean dryRun, Map<String, Long> sectionIdBySlug) {
        String sheetName = ImportSheetName.SECTIONS.sheetName();
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            return notPresent(sheetName);
        }

        Map<String, Integer> h = headerIndex(sheet);
        List<ImportRowError> errors = new ArrayList<>();
        int total = 0;
        int succeeded = 0;
        long placeholderId = -1;

        for (int r = sheet.getFirstRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (isBlankRow(row)) {
                continue;
            }
            total++;
            List<String> rowErrors = new ArrayList<>();

            String slug = str(row, h, "slug");
            String namePt = str(row, h, "name_pt");
            String nameEn = str(row, h, "name_en");
            SectionType sectionType = parseEnum(SectionType.class, row, h, "section_type", rowErrors);
            String descriptionPt = str(row, h, "description_pt");
            String descriptionEn = str(row, h, "description_en");
            String coverImageUrl = str(row, h, "cover_image_url");
            String coverVideoUrl = str(row, h, "cover_video_url");
            String displayVariant = str(row, h, "display_variant");
            String layoutStyle = str(row, h, "layout_style");
            Boolean showIntro = parseBool(row, h, "show_intro", true, rowErrors);
            Boolean showGallery = parseBool(row, h, "show_gallery", false, rowErrors);
            Boolean showFilters = parseBool(row, h, "show_filters", false, rowErrors);
            Boolean showItemDetails = parseBool(row, h, "show_item_details", true, rowErrors);
            String detailsViewMode = str(row, h, "details_view_mode");
            Boolean allowCustomAttributes = parseBool(row, h, "allow_custom_attributes", true, rowErrors);
            Boolean isActive = parseBool(row, h, "is_active", true, rowErrors);
            Integer sortOrder = parseInt(row, h, "sort_order", 0, rowErrors);

            if (slug == null) {
                rowErrors.add("slug is required");
            }
            if (namePt == null) {
                rowErrors.add("name_pt is required");
            }
            if (nameEn == null) {
                rowErrors.add("name_en is required");
            }
            String normalizedSlug = slug == null ? null : slug.trim().toLowerCase();
            if (normalizedSlug != null && sectionIdBySlug.containsKey(normalizedSlug)) {
                rowErrors.add("slug '" + slug + "' is already used by another section");
            }

            if (!rowErrors.isEmpty()) {
                errors.add(new ImportRowError(r + 1, String.join("; ", rowErrors)));
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
            request.setDisplayVariant(displayVariant);
            request.setLayoutStyle(layoutStyle);
            request.setShowIntro(showIntro);
            request.setShowGallery(showGallery);
            request.setShowFilters(showFilters);
            request.setShowItemDetails(showItemDetails);
            request.setDetailsViewMode(detailsViewMode);
            request.setAllowCustomAttributes(allowCustomAttributes);
            request.setIsActive(isActive);
            request.setSortOrder(sortOrder);

            try {
                Long id = dryRun ? placeholderId-- : sectionService.create(request).getId();
                sectionIdBySlug.put(normalizedSlug, id);
                succeeded++;
            } catch (RuntimeException e) {
                errors.add(new ImportRowError(r + 1, e.getMessage()));
            }
        }

        return new ImportSheetResult(sheetName, true, total, succeeded, errors);
    }

    // -------------------------------------------------------------- Categories

    private ImportSheetResult processCategories(
            Workbook workbook, boolean dryRun,
            Map<String, Long> sectionIdBySlug, Map<String, Long> categoryIdByKey
    ) {
        String sheetName = ImportSheetName.CATEGORIES.sheetName();
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            return notPresent(sheetName);
        }

        Map<String, Integer> h = headerIndex(sheet);
        List<ImportRowError> errors = new ArrayList<>();
        int total = 0;
        int succeeded = 0;
        long placeholderId = -1;

        for (int r = sheet.getFirstRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (isBlankRow(row)) {
                continue;
            }
            total++;
            List<String> rowErrors = new ArrayList<>();

            String sectionSlug = str(row, h, "section_slug");
            String namePt = str(row, h, "name_pt");
            String nameEn = str(row, h, "name_en");
            String descriptionPt = str(row, h, "description_pt");
            String descriptionEn = str(row, h, "description_en");
            Boolean isActive = parseBool(row, h, "is_active", true, rowErrors);
            Integer sortOrder = parseInt(row, h, "sort_order", 0, rowErrors);

            Long sectionId = resolveSectionId(sectionSlug, sectionIdBySlug, rowErrors);
            if (namePt == null) {
                rowErrors.add("name_pt is required");
            }
            if (nameEn == null) {
                rowErrors.add("name_en is required");
            }

            if (!rowErrors.isEmpty()) {
                errors.add(new ImportRowError(r + 1, String.join("; ", rowErrors)));
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
                succeeded++;
            } catch (RuntimeException e) {
                errors.add(new ImportRowError(r + 1, e.getMessage()));
            }
        }

        return new ImportSheetResult(sheetName, true, total, succeeded, errors);
    }

    // ------------------------------------------------------------------ Items

    private ImportSheetResult processItems(
            Workbook workbook, boolean dryRun,
            Map<String, Long> sectionIdBySlug, Map<String, Long> categoryIdByKey
    ) {
        String sheetName = ImportSheetName.ITEMS.sheetName();
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            return notPresent(sheetName);
        }

        Map<String, Integer> h = headerIndex(sheet);
        List<ImportRowError> errors = new ArrayList<>();
        int total = 0;
        int succeeded = 0;

        for (int r = sheet.getFirstRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (isBlankRow(row)) {
                continue;
            }
            total++;
            List<String> rowErrors = new ArrayList<>();

            String sectionSlug = str(row, h, "section_slug");
            String categoryNameEn = str(row, h, "category_name_en");
            String titlePt = str(row, h, "title_pt");
            String titleEn = str(row, h, "title_en");
            String shortDescriptionPt = str(row, h, "short_description_pt");
            String shortDescriptionEn = str(row, h, "short_description_en");
            String fullDescriptionPt = str(row, h, "full_description_pt");
            String fullDescriptionEn = str(row, h, "full_description_en");
            String coverImageUrl = str(row, h, "cover_image_url");
            String videoUrl = str(row, h, "video_url");
            String itemType = str(row, h, "item_type");
            String specificationsPt = str(row, h, "specifications_pt");
            String specificationsEn = str(row, h, "specifications_en");
            Boolean isFeatured = parseBool(row, h, "is_featured", false, rowErrors);
            Boolean isActive = parseBool(row, h, "is_active", true, rowErrors);
            Integer sortOrder = parseInt(row, h, "sort_order", 0, rowErrors);

            Long sectionId = resolveSectionId(sectionSlug, sectionIdBySlug, rowErrors);
            Long categoryId = resolveCategoryId(categoryNameEn, sectionId, categoryIdByKey, rowErrors);
            if (titlePt == null) {
                rowErrors.add("title_pt is required");
            }
            if (titleEn == null) {
                rowErrors.add("title_en is required");
            }

            if (!rowErrors.isEmpty()) {
                errors.add(new ImportRowError(r + 1, String.join("; ", rowErrors)));
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
                errors.add(new ImportRowError(r + 1, e.getMessage()));
            }
        }

        return new ImportSheetResult(sheetName, true, total, succeeded, errors);
    }

    // -------------------------------------------------------- PortfolioProjects

    private ImportSheetResult processPortfolioProjects(Workbook workbook, boolean dryRun, Map<String, Long> sectionIdBySlug) {
        String sheetName = ImportSheetName.PORTFOLIO_PROJECTS.sheetName();
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            return notPresent(sheetName);
        }

        Map<String, Integer> h = headerIndex(sheet);
        List<ImportRowError> errors = new ArrayList<>();
        int total = 0;
        int succeeded = 0;

        for (int r = sheet.getFirstRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (isBlankRow(row)) {
                continue;
            }
            total++;
            List<String> rowErrors = new ArrayList<>();

            String sectionSlug = str(row, h, "section_slug");
            String titlePt = str(row, h, "title_pt");
            String titleEn = str(row, h, "title_en");
            String shortDescriptionPt = str(row, h, "short_description_pt");
            String shortDescriptionEn = str(row, h, "short_description_en");
            String fullDescriptionPt = str(row, h, "full_description_pt");
            String fullDescriptionEn = str(row, h, "full_description_en");
            String clientName = str(row, h, "client_name");
            LocalDate projectDate = parseDate(row, h, "project_date", rowErrors);
            String locationPt = str(row, h, "location_pt");
            String locationEn = str(row, h, "location_en");
            String coverImageUrl = str(row, h, "cover_image_url");
            String videoUrl = str(row, h, "video_url");
            Boolean isFeatured = parseBool(row, h, "is_featured", false, rowErrors);
            Boolean isActive = parseBool(row, h, "is_active", true, rowErrors);
            Integer sortOrder = parseInt(row, h, "sort_order", 0, rowErrors);

            Long sectionId = resolveSectionId(sectionSlug, sectionIdBySlug, rowErrors);
            if (titlePt == null) {
                rowErrors.add("title_pt is required");
            }
            if (titleEn == null) {
                rowErrors.add("title_en is required");
            }

            if (!rowErrors.isEmpty()) {
                errors.add(new ImportRowError(r + 1, String.join("; ", rowErrors)));
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
                errors.add(new ImportRowError(r + 1, e.getMessage()));
            }
        }

        return new ImportSheetResult(sheetName, true, total, succeeded, errors);
    }

    // ----------------------------------------------------------- ContentBlocks

    private ImportSheetResult processContentBlocks(Workbook workbook, boolean dryRun, Map<String, Long> sectionIdBySlug) {
        String sheetName = ImportSheetName.CONTENT_BLOCKS.sheetName();
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            return notPresent(sheetName);
        }

        Map<String, Integer> h = headerIndex(sheet);
        List<ImportRowError> errors = new ArrayList<>();
        int total = 0;
        int succeeded = 0;

        for (int r = sheet.getFirstRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (isBlankRow(row)) {
                continue;
            }
            total++;
            List<String> rowErrors = new ArrayList<>();

            String sectionSlug = str(row, h, "section_slug");
            ContentBlockType blockType = parseEnum(ContentBlockType.class, row, h, "block_type", rowErrors);
            String titlePt = str(row, h, "title_pt");
            String titleEn = str(row, h, "title_en");
            String subtitlePt = str(row, h, "subtitle_pt");
            String subtitleEn = str(row, h, "subtitle_en");
            String contentPt = str(row, h, "content_pt");
            String contentEn = str(row, h, "content_en");
            String imageUrl = str(row, h, "image_url");
            String videoUrl = str(row, h, "video_url");
            Boolean isActive = parseBool(row, h, "is_active", true, rowErrors);
            Integer sortOrder = parseInt(row, h, "sort_order", 0, rowErrors);

            Long sectionId = resolveSectionId(sectionSlug, sectionIdBySlug, rowErrors);

            if (!rowErrors.isEmpty()) {
                errors.add(new ImportRowError(r + 1, String.join("; ", rowErrors)));
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
                errors.add(new ImportRowError(r + 1, e.getMessage()));
            }
        }

        return new ImportSheetResult(sheetName, true, total, succeeded, errors);
    }

    // --------------------------------------------------------------- HomeCards

    private ImportSheetResult processHomeCards(Workbook workbook, boolean dryRun, Map<String, Long> sectionIdBySlug) {
        String sheetName = ImportSheetName.HOME_CARDS.sheetName();
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            return notPresent(sheetName);
        }

        Map<String, Integer> h = headerIndex(sheet);
        List<ImportRowError> errors = new ArrayList<>();
        int total = 0;
        int succeeded = 0;

        for (int r = sheet.getFirstRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (isBlankRow(row)) {
                continue;
            }
            total++;
            List<String> rowErrors = new ArrayList<>();

            String sectionSlug = str(row, h, "section_slug");
            String titlePt = str(row, h, "title_pt");
            String titleEn = str(row, h, "title_en");
            String shortDescriptionPt = str(row, h, "short_description_pt");
            String shortDescriptionEn = str(row, h, "short_description_en");
            String imageUrl = str(row, h, "image_url");
            String iconName = str(row, h, "icon_name");
            Boolean isActive = parseBool(row, h, "is_active", true, rowErrors);
            Integer sortOrder = parseInt(row, h, "sort_order", 0, rowErrors);

            Long sectionId = resolveSectionId(sectionSlug, sectionIdBySlug, rowErrors);
            if (titlePt == null) {
                rowErrors.add("title_pt is required");
            }
            if (titleEn == null) {
                rowErrors.add("title_en is required");
            }

            if (!rowErrors.isEmpty()) {
                errors.add(new ImportRowError(r + 1, String.join("; ", rowErrors)));
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
                errors.add(new ImportRowError(r + 1, e.getMessage()));
            }
        }

        return new ImportSheetResult(sheetName, true, total, succeeded, errors);
    }

    // ---------------------------------------------------------- ContactMethods

    private ImportSheetResult processContactMethods(Workbook workbook, boolean dryRun) {
        String sheetName = ImportSheetName.CONTACT_METHODS.sheetName();
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            return notPresent(sheetName);
        }

        Map<String, Integer> h = headerIndex(sheet);
        List<ImportRowError> errors = new ArrayList<>();
        int total = 0;
        int succeeded = 0;

        for (int r = sheet.getFirstRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (isBlankRow(row)) {
                continue;
            }
            total++;
            List<String> rowErrors = new ArrayList<>();

            ContactMethodType type = parseEnum(ContactMethodType.class, row, h, "type", rowErrors);
            String labelPt = str(row, h, "label_pt");
            String labelEn = str(row, h, "label_en");
            String value = str(row, h, "value");
            String iconName = str(row, h, "icon_name");
            Boolean isActive = parseBool(row, h, "is_active", true, rowErrors);
            Integer sortOrder = parseInt(row, h, "sort_order", 0, rowErrors);

            if (value == null) {
                rowErrors.add("value is required");
            }

            if (!rowErrors.isEmpty()) {
                errors.add(new ImportRowError(r + 1, String.join("; ", rowErrors)));
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
                errors.add(new ImportRowError(r + 1, e.getMessage()));
            }
        }

        return new ImportSheetResult(sheetName, true, total, succeeded, errors);
    }

    // ------------------------------------------------------------------ shared

    private String categoryKey(Long sectionId, String nameEn) {
        return sectionId + "::" + nameEn.trim().toLowerCase();
    }

    private Long resolveSectionId(String slugRaw, Map<String, Long> sectionIdBySlug, List<String> rowErrors) {
        if (slugRaw == null) {
            rowErrors.add("section_slug is required");
            return null;
        }
        Long id = sectionIdBySlug.get(slugRaw.trim().toLowerCase());
        if (id == null) {
            rowErrors.add("no section found with slug '" + slugRaw
                    + "' (add it to the Sections sheet, or use an existing slug)");
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

    private ImportSheetResult notPresent(String sheetName) {
        return new ImportSheetResult(sheetName, false, 0, 0, List.of());
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

    private Boolean parseBool(Row row, Map<String, Integer> h, String field, Boolean defaultVal, List<String> rowErrors) {
        String raw = str(row, h, field);
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

    private Integer parseInt(Row row, Map<String, Integer> h, String field, Integer defaultVal, List<String> rowErrors) {
        String raw = str(row, h, field);
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

    private LocalDate parseDate(Row row, Map<String, Integer> h, String field, List<String> rowErrors) {
        String raw = str(row, h, field);
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

    private <E extends Enum<E>> E parseEnum(Class<E> type, Row row, Map<String, Integer> h, String field, List<String> rowErrors) {
        String raw = str(row, h, field);
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
