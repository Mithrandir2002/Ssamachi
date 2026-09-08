package com.earthquake.core.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record CreateReportRequest(
        Map<String, Object> filters,
        @NotBlank String format
) {
}
