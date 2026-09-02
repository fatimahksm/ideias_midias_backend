-- The admin and public listings now filter and sort in the database. Postgres
-- does not index foreign keys on its own, so without these every filtered page
-- is a sequential scan over the whole table.

CREATE INDEX IF NOT EXISTS idx_section_items_section_sort
    ON section_items (section_id, sort_order, id);

CREATE INDEX IF NOT EXISTS idx_section_items_category
    ON section_items (category_id);

CREATE INDEX IF NOT EXISTS idx_section_items_is_active
    ON section_items (is_active);

CREATE INDEX IF NOT EXISTS idx_portfolio_projects_section_sort
    ON portfolio_projects (section_id, sort_order, id);

CREATE INDEX IF NOT EXISTS idx_portfolio_projects_is_active
    ON portfolio_projects (is_active);

CREATE INDEX IF NOT EXISTS idx_media_library_file_type
    ON media_library (file_type, id DESC);

CREATE INDEX IF NOT EXISTS idx_media_library_uploaded_by
    ON media_library (uploaded_by, id DESC);
