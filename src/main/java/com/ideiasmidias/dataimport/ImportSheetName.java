package com.ideiasmidias.dataimport;

/**
 * The sheets an import workbook may contain, in the order they must be
 * processed: every sheet that references a Section or Category depends on
 * that sheet already having run, including references to rows created
 * earlier in the very same workbook.
 */
public enum ImportSheetName {

    SECTIONS("Sections"),
    CATEGORIES("Categories"),
    ITEMS("Items"),
    PORTFOLIO_PROJECTS("PortfolioProjects"),
    CONTENT_BLOCKS("ContentBlocks"),
    HOME_CARDS("HomeCards"),
    CONTACT_METHODS("ContactMethods");

    private final String sheetName;

    ImportSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String sheetName() {
        return sheetName;
    }
}
