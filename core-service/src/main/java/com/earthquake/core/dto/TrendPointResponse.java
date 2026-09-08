package com.earthquake.core.dto;

public record TrendPointResponse(
        String bucket,
        long count,
        Double averageMagnitude
) {
}
