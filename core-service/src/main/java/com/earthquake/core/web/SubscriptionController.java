package com.earthquake.core.web;

import com.earthquake.core.dto.CreateSubscriptionRequest;
import com.earthquake.core.dto.NotificationResponse;
import com.earthquake.core.dto.SubscriptionResponse;
import com.earthquake.core.dto.UpdateSubscriptionRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    @GetMapping
    public ResponseEntity<List<SubscriptionResponse>> list() {
        throw new UnsupportedOperationException("TODO: implement list subscriptions");
    }

    @PostMapping
    public ResponseEntity<SubscriptionResponse> create(@RequestBody @Valid CreateSubscriptionRequest request) {
        throw new UnsupportedOperationException("TODO: implement create subscription");
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateSubscriptionRequest request
    ) {
        throw new UnsupportedOperationException("TODO: implement update subscription");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        throw new UnsupportedOperationException("TODO: implement delete subscription");
    }

    @GetMapping("/{id}/notifications")
    public ResponseEntity<List<NotificationResponse>> notifications(@PathVariable Long id) {
        throw new UnsupportedOperationException("TODO: implement list notifications for subscription");
    }
}
