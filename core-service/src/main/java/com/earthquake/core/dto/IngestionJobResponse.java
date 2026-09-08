package com.earthquake.core.dto;

import java.time.LocalDateTime;

public record IngestionJobResponse(
        Long id,
        String jobType,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String status,
        int recordsProcessed,
        String errorMessage,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
