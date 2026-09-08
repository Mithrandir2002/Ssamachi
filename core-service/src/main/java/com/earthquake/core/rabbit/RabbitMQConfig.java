package com.earthquake.core.rabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ALERTS_EXCHANGE = "alerts.exchange";

    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String NOTIFICATION_ROUTING_KEY = "notification.send";
    public static final String NOTIFICATION_DLQ = "notification.queue.dlq";

    public static final String REPORT_QUEUE = "report.queue";
    public static final String REPORT_ROUTING_KEY = "report.generate";
    public static final String REPORT_DLQ = "report.queue.dlq";

    @Bean
    public DirectExchange alertsExchange() {
        return new DirectExchange(ALERTS_EXCHANGE);
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", NOTIFICATION_DLQ)
                .build();
    }

    @Bean
    public Queue notificationDlq() {
        return QueueBuilder.durable(NOTIFICATION_DLQ).build();
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue()).to(alertsExchange()).with(NOTIFICATION_ROUTING_KEY);
    }

    @Bean
    public Queue reportQueue() {
        return QueueBuilder.durable(REPORT_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", REPORT_DLQ)
                .build();
    }

    @Bean
    public Queue reportDlq() {
        return QueueBuilder.durable(REPORT_DLQ).build();
    }

    @Bean
    public Binding reportBinding() {
        return BindingBuilder.bind(reportQueue()).to(alertsExchange()).with(REPORT_ROUTING_KEY);
    }
}
