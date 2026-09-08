package com.earthquake.auth.rabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class EmailVerificationProducer {

    private final RabbitTemplate rabbitTemplate;

    public EmailVerificationProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // TODO: implement — rabbitTemplate.convertAndSend(RabbitMQConfig.AUTH_EXCHANGE,
    // RabbitMQConfig.EMAIL_VERIFICATION_ROUTING_KEY, message)
    public void publish(EmailVerificationMessage message) {
        throw new UnsupportedOperationException("TODO: implement publish");
    }
}
