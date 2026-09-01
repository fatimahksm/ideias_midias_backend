ALTER TABLE media_library
    ADD COLUMN processing_status VARCHAR(20) NOT NULL DEFAULT 'READY';

ALTER TABLE media_library
    ADD CONSTRAINT media_library_processing_status_check
    CHECK (processing_status IN ('READY', 'PROCESSING', 'FAILED'));
