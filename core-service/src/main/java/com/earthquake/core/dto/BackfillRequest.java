package com.earthquake.core.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

/**
 * @param minMagnitude null falls back to {@code usgs.default-min-magnitude}. This is the
 *                     single biggest lever on how much data the run pulls: 2.5 yields
 *                     roughly 190k events since 2020, 1.0 yields roughly 740k.
 */
public record BackfillRequest(

        @NotNull @PastOrPresent LocalDate startDate,

        @NotNull @PastOrPresent LocalDate endDate,

        Double minMagnitude
) {
    public boolean hasValidRange() {
        return startDate != null && endDate != null && startDate.isBefore(endDate);
    }
}
