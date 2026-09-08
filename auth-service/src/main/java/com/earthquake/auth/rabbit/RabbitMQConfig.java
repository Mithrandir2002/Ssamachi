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

/**
 * auth-service is both publisher and consumer here — this queue is only ever
 * produced to and consumed by this same service, so no separate notification
 * service is needed for it.
 */
@Configuration
public class RabbitMQConfig {

    public static final String AUTH_EXCHANGE = "auth.exchange";
    public static final String EMAIL_VERIFICATION_QUEUE = "email-verification.queue";
    public static final String EMAIL_VERIFICATION_ROUTING_KEY = "email.verification";
    public static final String EMAIL_VERIFICATION_DLQ = "email-verification.queue.dlq";
    private static final String AUTH_DEAD_LETTER_EXCHANGE = "auth.exchange.dlx";

    @Bean
    public DirectExchange authExchange() {
        return new DirectExchange(AUTH_EXCHANGE);
    }

    @Bean
    public DirectExchange authDeadLetterExchange() {
        return new DirectExchange(AUTH_DEAD_LETTER_EXCHANGE);
    }

    @Bean
    public Queue emailVerificationQueue() {
        return QueueBuilder.durable(EMAIL_VERIFICATION_QUEUE)
                .withArgument("x-dead-letter-exchange", AUTH_DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", EMAIL_VERIFICATION_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue emailVerificationDeadLetterQueue() {
        return QueueBuilder.durable(EMAIL_VERIFICATION_DLQ).build();
    }

    @Bean
    public Binding emailVerificationBinding() {
        return BindingBuilder.bind(emailVerificationQueue())
                .to(authExchange())
                .with(EMAIL_VERIFICATION_ROUTING_KEY);
    }

    @Bean
    public Binding emailVerificationDeadLetterBinding() {
        return BindingBuilder.bind(emailVerificationDeadLetterQueue())
                .to(authDeadLetterExchange())
                .with(EMAIL_VERIFICATION_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
