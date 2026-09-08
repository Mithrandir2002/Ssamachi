package com.earthquake.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "alert_subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId; // no FK: users live in a different service/db

    @Column(length = 255)
    private String name;

    @Column(name = "center_lat", nullable = false)
    private double centerLat;

    @Column(name = "center_lon", nullable = false)
    private double centerLon;

    @Column(name = "radius_km", nullable = false)
    private double radiusKm;

    @Column(name = "min_magnitude", nullable = false)
    private double minMagnitude;

    @Column(nullable = false, length = 20)
    private String channel;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
