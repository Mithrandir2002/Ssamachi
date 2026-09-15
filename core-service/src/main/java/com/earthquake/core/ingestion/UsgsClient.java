package com.earthquake.core.ingestion;

import com.earthquake.core.config.UsgsProperties;
import com.earthquake.core.ingestion.dto.UsgsFeature;
import com.earthquake.core.ingestion.dto.UsgsFeatureCollection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class UsgsClient {

    /**
     * FDSN expects plain UTC timestamps without a zone suffix, e.g. 2020-01-01T00:00:00.
     * Instant.toString() would append a "Z", which is not part of the spec.
     */
    private static final DateTimeFormatter FDSN_TIME = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            .withZone(ZoneOffset.UTC);

    private final RestTemplate restTemplate;
    private final UsgsProperties properties;

    public UsgsClient(RestTemplateBuilder builder, UsgsProperties properties) {
        this.properties = properties;
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofMillis(properties.connectTimeoutMs()))
                .setReadTimeout(Duration.ofMillis(properties.readTimeoutMs()))
                .defaultHeader(HttpHeaders.USER_AGENT, properties.userAgent())
                .build();
    }

    /**
     * Pre-generated static feed covering the last hour. Cheap enough to poll often;
     * overlapping windows are expected and are resolved by upserting on event id.
     */
    /**
     * hàm này gọi ở method đánh dấu @Schedule để sync data từ hiện tại
     */
    public List<UsgsFeature> fetchRealtimeFeed() {
        UsgsFeatureCollection body = restTemplate.getForObject(
                properties.realtimeFeedUrl(), UsgsFeatureCollection.class);

        List<UsgsFeature> features = featuresOf(body);
        log.debug("USGS realtime feed returned {} features", features.size());
        return features;
    }

    /**
     * Historical query. The window must be small enough to stay under the FDSN cap of
     * {@code maxEventsPerQuery} events — a query that would exceed it is rejected with
     * HTTP 400 rather than truncated, so the caller should catch that and split the
     * window instead of silently losing events.
     */
    /**
     *hàm này để backfill lịch sử động đất
     */
    public List<UsgsFeature> queryEvents(Instant start, Instant end, double minMagnitude) {
        URI uri = UriComponentsBuilder.fromUriString(properties.fdsnBaseUrl())
                .queryParam("format", "geojson")
                .queryParam("starttime", FDSN_TIME.format(start))
                .queryParam("endtime", FDSN_TIME.format(end))
                .queryParam("minmagnitude", minMagnitude)
                .queryParam("orderby", "time-asc")
                .build()
                .encode()
                .toUri();

        ResponseEntity<UsgsFeatureCollection> response =
                restTemplate.getForEntity(uri, UsgsFeatureCollection.class);

        // FDSN answers 204 No Content (empty body) when nothing matches — a normal
        // outcome for a quiet window, not an error.
        List<UsgsFeature> features = featuresOf(response.getBody());

        log.info("USGS query {} -> {} : {} events (M>={})",
                FDSN_TIME.format(start), FDSN_TIME.format(end), features.size(), minMagnitude);

        return features;
    }

    private List<UsgsFeature> featuresOf(UsgsFeatureCollection body) {
        if (body == null || body.getFeatures() == null) {
            return List.of();
        }
        return body.getFeatures();
    }
}
