package com.earthquake.core.repository;

import com.earthquake.core.domain.IngestionJob;
import com.earthquake.core.domain.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngestionJobRepository extends JpaRepository<IngestionJob, Long> {

    /** Guards against a second backfill being started while one is still running. */
    boolean existsByStatus(JobStatus status);

    List<IngestionJob> findAllByOrderByCreatedAtDesc();

    /**
     * Adds to the counter inside SQL rather than reading the row, adding in Java and
     * writing it back: several chunk threads finish at once, and a read-modify-write
     * would let them overwrite each other's increments.
     */
    @Modifying
    @Query(value = """
            UPDATE ingestion_jobs
            SET records_processed = records_processed + :count,
                updated_at = SYSUTCDATETIME()
            WHERE id = :id
            """, nativeQuery = true)
    void addRecordsProcessed(@Param("id") Long id, @Param("count") int count);
}
