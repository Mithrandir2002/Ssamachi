package com.earthquake.core.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String RAW_EARTHQUAKES_TOPIC = "raw-earthquakes";
    public static final String PROCESSED_EARTHQUAKES_TOPIC = "processed-earthquakes";

    @Bean
    public NewTopic rawEarthquakesTopic() {
        return TopicBuilder.name(RAW_EARTHQUAKES_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic processedEarthquakesTopic() {
        return TopicBuilder.name(PROCESSED_EARTHQUAKES_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
