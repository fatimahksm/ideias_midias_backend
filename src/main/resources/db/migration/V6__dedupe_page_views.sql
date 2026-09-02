-- Supports the dedupe lookup "did this visitor already open this page in the
-- last N minutes?", which runs on every reported page view.
CREATE INDEX idx_page_views_visitor_path_viewed_at
    ON page_views (visitor_hash, path, viewed_at DESC);
