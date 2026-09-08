package com.earthquake.core.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReportQueueListener {

    @RabbitListener(queues = RabbitMQConfig.REPORT_QUEUE)
    public void onReportMessage(Object payload) {
        // TODO: generate the requested report for payload
        log.debug("Received message on {}: {}", RabbitMQConfig.REPORT_QUEUE, payload);
    }
}
