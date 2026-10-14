package com.preesa.razorpay.operations_service.outbox;


import com.preesa.razorpay.common_lib.enums.EventAggregateType;
import com.preesa.razorpay.operations_service.entity.OutBoxEvent;

import com.preesa.razorpay.operations_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutBoxEventPublisher {

    private final OutboxEventRepository outboxEventRepository;

    public void publish(EventAggregateType aggregateType, UUID aggregateId,
                        String eventType, Map<String,Object> payload){
        OutBoxEvent outBoxEvent = OutBoxEvent.builder()
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payload(payload)
                .build();

        outboxEventRepository.save(outBoxEvent);
    }
}
