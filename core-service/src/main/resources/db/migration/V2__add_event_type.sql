-- USGS feeds carry more than earthquakes: quarry blast, explosion, ice quake...
-- Keep the classification instead of filtering it away at ingestion time, so the
-- data stays complete and callers can decide what to include.
ALTER TABLE earthquakes ADD event_type VARCHAR(30) NULL;
