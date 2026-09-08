package com.earthquake.core.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProcessedEarthquakeConsumer {

    @KafkaListener(topics = KafkaTopicConfig.PROCESSED_EARTHQUAKES_TOPIC, groupId = "core-service")
    public void onProcessedEarthquake(Object payload) {
        // TODO: persist/react to the processed earthquake event
        log.debug("Received message on {}: {}", KafkaTopicConfig.PROCESSED_EARTHQUAKES_TOPIC, payload);
    }
}
