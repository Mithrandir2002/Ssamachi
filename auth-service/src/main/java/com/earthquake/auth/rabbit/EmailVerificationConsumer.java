package com.earthquake.auth.rabbit;

import com.earthquake.auth.service.MailService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class EmailVerificationConsumer {

    private final MailService mailService;
    private final RabbitTemplate rabbitTemplate;

    public EmailVerificationConsumer(MailService mailService, RabbitTemplate rabbitTemplate) {
        this.mailService = mailService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_VERIFICATION_QUEUE)
    public void onMessage(EmailVerificationMessage payload,
                          Message rawMessage,
                          Channel channel,
                          @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            mailService.sendVerificationCode(payload.email(), payload.code());
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            int attempt = failedAttempts(rawMessage) + 1;

            if (attempt < RabbitMQConfig.MAX_ATTEMPTS) {
                log.warn("Sending verification mail to {} failed (attempt {}/{}), retrying in {}s",
                        payload.email(), attempt, RabbitMQConfig.MAX_ATTEMPTS,
                        RabbitMQConfig.RETRY_DELAY_MS / 1000, e);
                // requeue=false -> the queue's DLX (the retry queue), where it waits out the TTL
                channel.basicNack(deliveryTag, false, false);
            } else {
                log.error("Sending verification mail to {} failed after {} attempts, parking in {}",
                        payload.email(), attempt, RabbitMQConfig.EMAIL_VERIFICATION_DLQ, e);
                // Out of retries: park it and ack, otherwise it would loop forever.
                rabbitTemplate.convertAndSend("", RabbitMQConfig.EMAIL_VERIFICATION_DLQ, payload);
                channel.basicAck(deliveryTag, false);
            }
        }
    }

    private int failedAttempts(Message rawMessage) {
        List<Map<String, ?>> xDeath = rawMessage.getMessageProperties().getXDeathHeader();
        if (xDeath == null) {
            return 0;
        }
        return xDeath.stream()
                .filter(death -> RabbitMQConfig.EMAIL_VERIFICATION_QUEUE.equals(death.get("queue")))
                .findFirst()
                .map(death -> death.get("count"))
                .filter(Number.class::isInstance)
                .map(count -> ((Number) count).intValue())
                .orElse(0);
    }
}
