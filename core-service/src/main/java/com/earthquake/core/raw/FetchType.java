package com.earthquake.core.raw;

/** How a raw payload was pulled from the source. */
public enum FetchType {

    /** Scheduled poll of the rolling "past hour" feed. No job row exists for these. */
    REALTIME,

    /** One time-window of an admin-triggered historical backfill, tied to an ingestion job. */
    BACKFILL
}
