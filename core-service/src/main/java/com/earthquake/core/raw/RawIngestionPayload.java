package com.earthquake.core.raw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document(collection = "raw_ingestion_payloads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawIngestionPayload {

    @Id
    private String id;

    private String source;

    private FetchType fetchType;

    private Map<String, Object> requestParams;

    private Map<String, Object> rawResponse;

    private Instant fetchedAt;

    /**
     * References {@code ingestion_jobs.id} in SQL Server — the backfill run this payload
     * belongs to. Null for {@link FetchType#REALTIME}: a scheduled poll is not a job, and
     * creating a row for each of the ~288 daily polls would be noise.
     */
    private Long jobId;
}
