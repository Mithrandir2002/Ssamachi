package com.earthquake.auth.rabbit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EmailVerificationConsumer {

    // TODO: inject MailService and call mailService.sendVerificationCode(message.email(), message.code())
    @RabbitListener(queues = RabbitMQConfig.EMAIL_VERIFICATION_QUEUE)
    public void onMessage(EmailVerificationMessage message) {
        throw new UnsupportedOperationException("TODO: implement onMessage");
    }
}
