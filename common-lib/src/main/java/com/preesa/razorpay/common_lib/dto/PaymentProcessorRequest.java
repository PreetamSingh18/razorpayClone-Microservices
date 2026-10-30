package com.preesa.razorpay.common_lib.dto;



import com.preesa.razorpay.common_lib.entity.Money;
import com.preesa.razorpay.common_lib.enums.PaymentMethod;

import java.util.Map;
import java.util.UUID;

public record PaymentProcessorRequest(
        UUID paymentId,
        UUID processingId,
        PaymentMethod method,
        Map<String, Object> methodDetails,
        String pan,
        String expiry,
        Money amount

) {
    public static PaymentProcessorRequest card(UUID paymentId,String pan, String expiry, Money amount, Map<String, Object> methodDetails) {
         return new PaymentProcessorRequest(paymentId, UUID.randomUUID(), PaymentMethod.CARD, methodDetails, pan, expiry, amount);

    }
    public static PaymentProcessorRequest noncard(UUID paymentId,PaymentMethod method,Money amount, Map<String, Object> methodDetails) {
        return new PaymentProcessorRequest( paymentId, UUID.randomUUID(), method, methodDetails, null, null, amount);

    }
}
