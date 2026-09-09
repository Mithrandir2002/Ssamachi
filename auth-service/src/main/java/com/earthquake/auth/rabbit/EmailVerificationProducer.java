package com.earthquake.auth.rabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static com.earthquake.auth.rabbit.RabbitMQConfig.EMAIL_VERIFICATION_ROUTING_KEY;

@Component
public class EmailVerificationProducer {

    private final RabbitTemplate rabbitTemplate;

    public EmailVerificationProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // TODO: implement — rabbitTemplate.convertAndSend(RabbitMQConfig.AUTH_EXCHANGE,
    // RabbitMQConfig.EMAIL_VERIFICATION_ROUTING_KEY, message)
    public void publish(EmailVerificationMessage message) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.AUTH_EXCHANGE, EMAIL_VERIFICATION_ROUTING_KEY, message);
    }
}
