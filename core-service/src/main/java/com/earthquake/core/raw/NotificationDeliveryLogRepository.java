package com.earthquake.core.raw;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationDeliveryLogRepository extends MongoRepository<NotificationDeliveryLog, String> {
}
