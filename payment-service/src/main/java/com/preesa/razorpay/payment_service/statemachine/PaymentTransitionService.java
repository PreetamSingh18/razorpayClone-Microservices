package com.preesa.razorpay.payment_service.statemachine;


import com.preesa.razorpay.common_lib.enums.PaymentActor;
import com.preesa.razorpay.common_lib.enums.PaymentEvent;
import com.preesa.razorpay.common_lib.enums.PaymentStatus;
import com.preesa.razorpay.payment_service.entity.Payment;
import com.preesa.razorpay.payment_service.entity.PaymentTransitionLog;
import com.preesa.razorpay.payment_service.repository.PaymentTransitionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PaymentTransitionService {

    private  final PaymentStateMachine paymentStateMachine;
    private final PaymentTransitionLogRepository paymentTransitionLogRepository;

    public PaymentStatus apply(Payment payment, PaymentEvent event){
        PaymentStatus nextStatus = paymentStateMachine.transition(payment.getStatus(),event);
        PaymentTransitionLog log = PaymentTransitionLog.builder()
                .actor(PaymentActor.SYSTEM) //TODO: Fetch Merchant context to identify actor - spring security.
                .toStatus(nextStatus)
                .fromStatus(payment.getStatus())
                .event(event)
                .payment(payment)
                .occurredAt(Instant.now())
                .build();

        payment.setStatus(nextStatus);
        paymentTransitionLogRepository.save(log);
        return nextStatus;
    }
}
