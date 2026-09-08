-- Core service schema (SQL Server)

CREATE TABLE earthquakes (
    id              VARCHAR(50) NOT NULL PRIMARY KEY,  -- USGS event id (natural key)
    magnitude       FLOAT NULL,
    place           NVARCHAR(500) NULL,
    event_time      DATETIME2 NOT NULL,
    updated_time    DATETIME2 NULL,
    depth_km        FLOAT NULL,
    latitude        FLOAT NOT NULL,
    longitude       FLOAT NOT NULL,
    location        GEOGRAPHY NULL,  -- populated by application code later via geography::Point(latitude, longitude, 4326), not a computed column
    magnitude_type  VARCHAR(10) NULL,
    status          VARCHAR(20) NULL,
    tsunami_flag    BIT NOT NULL CONSTRAINT DF_earthquakes_tsunami_flag DEFAULT 0,
    felt_reports    INT NULL,
    ingested_at     DATETIME2 NOT NULL CONSTRAINT DF_earthquakes_ingested_at DEFAULT SYSUTCDATETIME()
);

CREATE INDEX IX_earthquakes_time_mag ON earthquakes (event_time DESC, magnitude DESC);

-- Spatial index on `location`. `location` starts out NULL for every row and is populated
-- by application code (geography::Point(latitude, longitude, 4326)) — not a computed column.
CREATE SPATIAL INDEX SIDX_earthquakes_location ON earthquakes(location) USING GEOGRAPHY_AUTO_GRID;

CREATE TABLE alert_subscriptions (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id         BIGINT NOT NULL,  -- no FK: users live in a different service/db
    name            NVARCHAR(255) NULL,
    center_lat      FLOAT NOT NULL,
    center_lon      FLOAT NOT NULL,
    radius_km       FLOAT NOT NULL,
    min_magnitude   FLOAT NOT NULL,
    channel         VARCHAR(20) NOT NULL,
    is_active       BIT NOT NULL CONSTRAINT DF_subscriptions_is_active DEFAULT 1,
    created_at      DATETIME2 NOT NULL CONSTRAINT DF_subscriptions_created_at DEFAULT SYSUTCDATETIME(),
    updated_at      DATETIME2 NULL
);

CREATE INDEX IX_subscriptions_user ON alert_subscriptions (user_id);
CREATE INDEX IX_subscriptions_active ON alert_subscriptions (is_active) WHERE is_active = 1;

CREATE TABLE alert_notifications (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    subscription_id BIGINT NOT NULL,
    earthquake_id   VARCHAR(50) NOT NULL,
    sent_at         DATETIME2 NULL,
    channel         VARCHAR(20) NOT NULL,
    status          VARCHAR(20) NOT NULL,
    CONSTRAINT FK_notifications_subscription FOREIGN KEY (subscription_id) REFERENCES alert_subscriptions(id),
    CONSTRAINT FK_notifications_earthquake FOREIGN KEY (earthquake_id) REFERENCES earthquakes(id)
);

CREATE INDEX IX_notifications_sub_time ON alert_notifications (subscription_id, sent_at DESC);

CREATE TABLE report_requests (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    filter_json     NVARCHAR(MAX) NULL,
    format          VARCHAR(10) NOT NULL,
    status          VARCHAR(20) NOT NULL CONSTRAINT DF_reports_status DEFAULT 'PENDING',
    file_path       NVARCHAR(500) NULL,
    requested_at    DATETIME2 NOT NULL CONSTRAINT DF_reports_requested_at DEFAULT SYSUTCDATETIME(),
    completed_at    DATETIME2 NULL
);

CREATE INDEX IX_reports_user_time ON report_requests (user_id, requested_at DESC);

CREATE TABLE ingestion_jobs (
    id                  BIGINT IDENTITY(1,1) PRIMARY KEY,
    job_type            VARCHAR(20) NOT NULL,
    start_time          DATETIME2 NULL,
    end_time            DATETIME2 NULL,
    status              VARCHAR(20) NOT NULL CONSTRAINT DF_jobs_status DEFAULT 'PENDING',
    records_processed   INT NOT NULL CONSTRAINT DF_jobs_records_processed DEFAULT 0,
    error_message       NVARCHAR(MAX) NULL,
    created_at          DATETIME2 NOT NULL CONSTRAINT DF_jobs_created_at DEFAULT SYSUTCDATETIME(),
    updated_at          DATETIME2 NULL
);

CREATE INDEX IX_jobs_status_time ON ingestion_jobs (status, created_at DESC);
