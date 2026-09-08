package com.earthquake.core.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationQueueListener {

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void onNotificationMessage(Object payload) {
        // TODO: dispatch the alert notification (email/SMS/push/websocket) for payload
        log.debug("Received message on {}: {}", RabbitMQConfig.NOTIFICATION_QUEUE, payload);
    }
}
