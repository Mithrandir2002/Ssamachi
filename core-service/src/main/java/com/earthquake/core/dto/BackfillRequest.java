package com.earthquake.core.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record BackfillRequest(
        @NotBlank String jobType,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
