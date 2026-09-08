package com.earthquake.core.repository;

import com.earthquake.core.domain.AlertSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertSubscriptionRepository extends JpaRepository<AlertSubscription, Long> {

    List<AlertSubscription> findByUserId(Long userId);
}
