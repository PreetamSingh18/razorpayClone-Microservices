package com.preesa.razorpay.operations_service.repository;


import com.preesa.razorpay.common_lib.enums.WebhookEventStatus;
import com.preesa.razorpay.operations_service.entity.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent,UUID> {

   List<WebhookEvent> findByStatusAndNextRetryAtBefore(WebhookEventStatus pending, Instant now);
    
}
