package com.earthquake.core.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendRawEarthquake(String key, Object payload) {
        // TODO: publish the raw earthquake payload to KafkaTopicConfig.RAW_EARTHQUAKES_TOPIC
        throw new UnsupportedOperationException("TODO: implement sendRawEarthquake");
    }
}
