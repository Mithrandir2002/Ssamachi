package com.earthquake.core.raw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document(collection = "notification_delivery_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDeliveryLog {

    @Id
    private String id;

    private Long subscriptionId;

    private String earthquakeId;

    private String channel;

    private Map<String, Object> payload;

    private String status;

    private Instant createdAt;
}
