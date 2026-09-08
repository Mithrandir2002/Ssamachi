package com.earthquake.core.dto;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        Long subscriptionId,
        String earthquakeId,
        LocalDateTime sentAt,
        String channel,
        String status
) {
}
