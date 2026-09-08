package com.earthquake.core.repository;

import com.earthquake.core.domain.AlertNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertNotificationRepository extends JpaRepository<AlertNotification, Long> {

    List<AlertNotification> findBySubscriptionId(Long subscriptionId);
}
