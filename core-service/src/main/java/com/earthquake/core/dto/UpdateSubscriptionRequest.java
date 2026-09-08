package com.earthquake.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateSubscriptionRequest(
        String name,
        @NotNull Double centerLat,
        @NotNull Double centerLon,
        @NotNull Double radiusKm,
        @NotNull Double minMagnitude,
        @NotBlank String channel,
        Boolean isActive
) {
}
