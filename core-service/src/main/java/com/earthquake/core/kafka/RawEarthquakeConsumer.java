package com.earthquake.core.kafka;


import com.earthquake.core.domain.Earthquake;
import com.earthquake.core.ingestion.dto.UsgsFeature;
import com.earthquake.core.mapper.UsgsFeatureMapper;
import com.earthquake.core.service.RawEarthquakeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RawEarthquakeConsumer {

    private final RawEarthquakeService earthquakeService;

    public RawEarthquakeConsumer(RawEarthquakeService earthquakeService) {
        this.earthquakeService = earthquakeService;
    }

    @KafkaListener(topics = KafkaTopicConfig.RAW_EARTHQUAKES_TOPIC, groupId = "core-service")
    public void rawEarthquake(UsgsFeature feature) {
        Earthquake earthquake = UsgsFeatureMapper.toEarthquake(feature);
        log.debug("Received message on {}: {}", KafkaTopicConfig.RAW_EARTHQUAKES_TOPIC, feature.getId());
        earthquakeService.saveEarthquake(earthquake);
    }
}
