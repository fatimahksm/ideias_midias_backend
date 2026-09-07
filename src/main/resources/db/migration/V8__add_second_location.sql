ALTER TABLE public.site_settings
    ADD COLUMN address2_pt text,
    ADD COLUMN address2_en text,
    ADD COLUMN map_embed_url2 text,
    ADD COLUMN location2_lat numeric(10,7),
    ADD COLUMN location2_lng numeric(10,7);
