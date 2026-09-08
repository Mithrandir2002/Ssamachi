package com.earthquake.core.dto;

import java.time.LocalDateTime;

public record EarthquakeResponse(
        String id,
        Double magnitude,
        String place,
        LocalDateTime eventTime,
        LocalDateTime updatedTime,
        Double depthKm,
        double latitude,
        double longitude,
        String magnitudeType,
        String status,
        boolean tsunamiFlag,
        Integer feltReports
) {
}
