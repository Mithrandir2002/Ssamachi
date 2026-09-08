package com.earthquake.core.dto;

import java.time.LocalDateTime;

public record SubscriptionResponse(
        Long id,
        Long userId,
        String name,
        double centerLat,
        double centerLon,
        double radiusKm,
        double minMagnitude,
        String channel,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
