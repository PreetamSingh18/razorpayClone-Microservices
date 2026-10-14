package com.preesa.razorpay.payment_service.dto.response;



import com.preesa.razorpay.common_lib.entity.Money;
import com.preesa.razorpay.common_lib.enums.PaymentMethod;
import com.preesa.razorpay.common_lib.enums.PaymentStatus;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;


public record PaymentResponse(
        UUID id,
        UUID merchantId,
        UUID orderId,
        Money amount,
        PaymentStatus paymentStatus,
        PaymentMethod method,
        Map<String, Object> methodDetails,
        String bankReference,
        String errorCode,
        String errorDescription,
        Instant settledAt,
        Instant createdAt
) {
}
