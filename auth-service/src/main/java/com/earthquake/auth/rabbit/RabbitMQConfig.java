package com.earthquake.auth.rabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * auth-service is both publisher and consumer here — this queue is only ever
 * produced to and consumed by this same service, so no separate notification
 * service is needed for it.
 */
@Configuration
public class RabbitMQConfig {

    public static final String AUTH_EXCHANGE = "auth.exchange";
    public static final String AUTH_RETRY_EXCHANGE = "auth.retry.exchange";

    public static final String EMAIL_VERIFICATION_QUEUE = "email-verification.queue";
    public static final String EMAIL_VERIFICATION_ROUTING_KEY = "email.verification";

    public static final String EMAIL_VERIFICATION_RETRY_QUEUE = "email-verification.retry.queue";
    public static final String EMAIL_VERIFICATION_RETRY_ROUTING_KEY = "email.verification.retry";

    public static final String EMAIL_VERIFICATION_DLQ = "email-verification.dlq";

    public static final long RETRY_DELAY_MS = Duration.ofSeconds(30).toMillis();

    public static final int MAX_ATTEMPTS = 3;

    @Bean
    public DirectExchange authExchange() {
        return new DirectExchange(AUTH_EXCHANGE);
    }

    @Bean
    public DirectExchange authRetryExchange() {
        return new DirectExchange(AUTH_RETRY_EXCHANGE);
    }

    @Bean
    public Queue emailVerificationQueue() {
        return QueueBuilder.durable(EMAIL_VERIFICATION_QUEUE)
                // basicNack(requeue=false) sends the message here -> the retry queue
                .withArgument("x-dead-letter-exchange", AUTH_RETRY_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", EMAIL_VERIFICATION_RETRY_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue emailVerificationRetryQueue() {
        return QueueBuilder.durable(EMAIL_VERIFICATION_RETRY_QUEUE)
                .withArgument("x-message-ttl", RETRY_DELAY_MS)
                .withArgument("x-dead-letter-exchange", AUTH_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", EMAIL_VERIFICATION_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue emailVerificationDlq() {
        return QueueBuilder.durable(EMAIL_VERIFICATION_DLQ).build();
    }

    @Bean
    public Binding emailVerificationBinding() {
        return BindingBuilder.bind(emailVerificationQueue())
                .to(authExchange())
                .with(EMAIL_VERIFICATION_ROUTING_KEY);
    }

    @Bean
    public Binding emailVerificationRetryBinding() {
        return BindingBuilder.bind(emailVerificationRetryQueue())
                .to(authRetryExchange())
                .with(EMAIL_VERIFICATION_RETRY_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
