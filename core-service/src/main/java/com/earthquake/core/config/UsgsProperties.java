package com.earthquake.core.config;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.LocalDate;


@ConfigurationProperties(prefix = "usgs")
public record UsgsProperties (
    String fdsnBaseUrl,
    String realtimeFeedUrl,
    boolean realtimeEnabled,
    long pollIntervalMs,
    int connectTimeoutMs,
    int readTimeoutMs,
    String userAgent,
    double defaultMinMagnitude,
    LocalDate backfillStartDate,
    int backfillChunkDays,
    int maxEventsPerQuery
){}
