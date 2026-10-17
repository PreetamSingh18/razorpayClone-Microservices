package com.preesa.razorpay.payment_service.statemachine;


import com.preesa.razorpay.common_lib.context.MerchantContext;
import com.preesa.razorpay.common_lib.enums.PaymentActor;
import com.preesa.razorpay.common_lib.enums.PaymentEvent;
import com.preesa.razorpay.common_lib.enums.PaymentStatus;
import com.preesa.razorpay.payment_service.entity.Payment;
import com.preesa.razorpay.payment_service.entity.PaymentTransitionLog;
import com.preesa.razorpay.payment_service.repository.PaymentTransitionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentTransitionService {

    private  final PaymentStateMachine paymentStateMachine;
    private final PaymentTransitionLogRepository paymentTransitionLogRepository;
    private final MerchantContext merchantContext;

    public PaymentStatus apply(Payment payment, PaymentEvent event){
        PaymentStatus nextStatus = paymentStateMachine.transition(payment.getStatus(),event);
    PaymentActor actor = getPaymentActor();
        PaymentTransitionLog log = PaymentTransitionLog.builder()
                .actor(actor) //TODO: Fetch Merchant context to identify actor - spring security.
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

    private PaymentActor getPaymentActor() {
        String keyId = merchantContext.getKeyId();
        UUID merchantId = merchantContext.getMerchantId();
        if(keyId != null && !keyId.isBlank()){
            return PaymentActor.CUSTOMER;
        }
        else if(merchantId != null && !merchantId.toString().isBlank()){
            return PaymentActor.MERCHANT;
        }
        
        return PaymentActor.SYSTEM;
}
