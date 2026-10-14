package com.preesa.razorpay.payment_service.processor;

import com.preesa.razorpay.common_lib.enums.PaymentMethod;
import com.preesa.razorpay.common_lib.dto.PaymentProcessorRequest;
import com.preesa.razorpay.common_lib.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentProcessorRouter {

    private final Map<PaymentMethod, PaymentProcessor> paymentProcessorMap;

    public PaymentProcessorResponse charge(PaymentProcessorRequest request) throws Exception {
        PaymentProcessor paymentProcessor= paymentProcessorMap.get(request.method());
        if (paymentProcessor == null) {
            throw new IllegalArgumentException("Processor for " + request.method() + "  method doesn't found");
        }
        return paymentProcessor.charge(request);
    }

}
