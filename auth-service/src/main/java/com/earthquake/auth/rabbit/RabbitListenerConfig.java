package com.earthquake.auth.rabbit;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitListenerConfig {

    private static final int CONCURRENT_CONSUMERS = 3;

    private static final int MAX_CONCURRENT_CONSUMERS = 6;

    private static final int PREFETCH_COUNT = 1;

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        factory.setConcurrentConsumers(CONCURRENT_CONSUMERS);
        factory.setMaxConcurrentConsumers(MAX_CONCURRENT_CONSUMERS);
        factory.setPrefetchCount(PREFETCH_COUNT);

        factory.setMessageConverter(messageConverter);

        return factory;
    }
}
