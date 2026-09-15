package com.earthquake.core.kafka;

import com.earthquake.core.ingestion.dto.UsgsFeature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publishes one earthquake as one message, keyed by its USGS event id.
     *
     * <p>The key is what makes revisions safe: every update USGS publishes for the same
     * event hashes to the same partition, so those messages are consumed in the order
     * they were produced. Without a key they would be spread across partitions and a
     * stale revision could overwrite a newer one.
     *
     * <p>The returned future completes once the broker acknowledges the write, which is
     * what the backfill uses to count how many events actually landed.
     */
    public CompletableFuture<SendResult<String, Object>> sendRawEarthquake(UsgsFeature feature) {
        String eventId = feature.getId();

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(KafkaTopicConfig.RAW_EARTHQUAKES_TOPIC, eventId, feature);

        // send() is asynchronous: without this, a broker rejection would vanish silently.
        future.whenComplete((result, throwable) -> {
            if (throwable != null) {
                log.warn("Failed to publish earthquake {} to {}",
                        eventId, KafkaTopicConfig.RAW_EARTHQUAKES_TOPIC, throwable);
            }
        });

        return future;
    }
}
