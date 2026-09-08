package com.earthquake.core.dto;

import java.time.LocalDateTime;

public record ReportResponse(
        Long id,
        Long userId,
        String filterJson,
        String format,
        String status,
        String filePath,
        LocalDateTime requestedAt,
        LocalDateTime completedAt
) {
}
