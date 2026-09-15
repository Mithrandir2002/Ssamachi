-- USGS significance score: magnitude, felt reports and estimated impact rolled into
-- one number (sample values run from single digits to ~2000). Its heavily skewed
-- distribution makes it a good ranking column for /api/stats/top-regions.
ALTER TABLE earthquakes ADD sig INT NULL;

-- When *our* row was last written. Deliberately distinct from updated_time, which is
-- when USGS last revised the event itself — the two move independently: a re-ingested
-- event updates this column while updated_time stays put.
ALTER TABLE earthquakes ADD row_updated_at DATETIME2 NULL;
