package com.earthquake.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "earthquakes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Earthquake {

    @Id
    @Column(length = 50)
    private String id; // USGS event id (natural key)

    private Double magnitude;

    @Column(length = 500)
    private String place;

    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "depth_km")
    private Double depthKm;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    // TODO: `location` is a SQL Server GEOGRAPHY column populated via
    // geography::Point(latitude, longitude, 4326) by application code.
    // Plain JPA cannot map the GEOGRAPHY type directly - handle it with native SQL
    // (e.g. a custom repository method / @Query using nativeQuery) when implementing writes/reads.

    @Column(name = "magnitude_type", length = 10)
    private String magnitudeType;

    @Column(length = 20)
    private String status;

    @Column(name = "tsunami_flag", nullable = false)
    private boolean tsunamiFlag;

    @Column(name = "felt_reports")
    private Integer feltReports;

    @Column(name = "ingested_at", nullable = false)
    private LocalDateTime ingestedAt;
}
