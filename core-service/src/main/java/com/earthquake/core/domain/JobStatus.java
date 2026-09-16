package com.earthquake.core.domain;

public enum JobStatus {

    /** Row created, waiting for a thread to pick it up. */
    PENDING,

    /** At least one time window is being fetched. */
    RUNNING,

    /** Every window completed without error. */
    SUCCESS,

    /**
     * At least one window failed. Which ones is recorded in
     * {@link IngestionJob#getErrorMessage()} so the run can be repeated for just those
     * ranges — safe to do, since ingestion upserts on event id.
     */
    FAILED
}
