package com.earthquake.core.mapper;

import com.earthquake.core.domain.Earthquake;
import com.earthquake.core.ingestion.dto.UsgsEventProperties;
import com.earthquake.core.ingestion.dto.UsgsFeature;
import com.earthquake.core.ingestion.dto.UsgsGeometry;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

/** Maps one USGS GeoJSON feature into the application's persistent earthquake model. */
public final class UsgsFeatureMapper {

    private UsgsFeatureMapper() {
    }

    public static Earthquake toEarthquake(UsgsFeature feature) {
        Objects.requireNonNull(feature, "USGS feature must not be null");

        UsgsEventProperties properties = Objects.requireNonNull(
                feature.getProperties(), "USGS feature properties must not be null");
        UsgsGeometry geometry = Objects.requireNonNull(
                feature.getGeometry(), "USGS feature geometry must not be null");

        Double latitude = required(geometry.latitude(), "latitude");
        Double longitude = required(geometry.longitude(), "longitude");
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        return Earthquake.builder()
                .id(Objects.requireNonNull(feature.getId(), "USGS feature id must not be null"))
                .magnitude(properties.getMag())
                .place(properties.getPlace())
                .eventTime(toUtcDateTime(properties.getTime(), "event time"))
                .updatedTime(toUtcDateTime(properties.getUpdated(), "updated time"))
                .depthKm(geometry.depthKm())
                .latitude(latitude)
                .longitude(longitude)
                .magnitudeType(properties.getMagType())
                .status(properties.getStatus())
                .tsunamiFlag(Integer.valueOf(1).equals(properties.getTsunami()))
                .feltReports(properties.getFelt())
                .eventType(properties.getType())
                .sig(properties.getSig())
                .ingestedAt(now)
                .rowUpdatedAt(now)
                .build();
    }

    private static LocalDateTime toUtcDateTime(Long epochMillis, String field) {
        if (epochMillis == null) {
            if ("event time".equals(field)) {
                throw new IllegalArgumentException("USGS " + field + " must not be null");
            }
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneOffset.UTC);
    }

    private static Double required(Double value, String field) {
        if (value == null) {
            throw new IllegalArgumentException("USGS " + field + " must not be null");
        }
        return value;
    }
}
