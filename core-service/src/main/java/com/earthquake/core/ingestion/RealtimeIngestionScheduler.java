package com.earthquake.core.ingestion;

import com.earthquake.core.ingestion.dto.UsgsFeature;
import com.earthquake.core.kafka.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Polls the USGS "past hour" feed and publishes every event it contains.
 *
 * <p>The feed window (1 hour) is far wider than the poll interval (5 minutes) on purpose:
 * each event is therefore seen about twelve times. That redundancy is what makes a missed
 * run — or a restart — self-healing, and it also re-collects events USGS has revised since.
 * Duplicates cost nothing because the consumer upserts on event id.
 *
 * <p>Disabled by setting {@code usgs.realtime-enabled=false}: the bean itself is then never
 * created, so no scheduled task is registered at all.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "usgs", name = "realtime-enabled", havingValue = "true", matchIfMissing = true)
public class RealtimeIngestionScheduler {

    private final UsgsClient usgsClient;
    private final KafkaProducerService kafkaProducerService;

    @Scheduled(fixedDelayString = "${usgs.poll-interval-ms}")
    public void pollRealtimeFeed() {

    }
}
