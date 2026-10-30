package com.preesa.razorpay.payment_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.preesa.razorpay.common_lib.entity.Money;
import com.preesa.razorpay.common_lib.enums.OrderStatus;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderResponse(
        UUID orderId,
        UUID merchantId,
        UUID customerId,
        Money amount,
        String receipt,
        Map<String, Object> notes,
        OrderStatus orderStatus,
        Integer attempts,
        Instant expireAt,
        Instant createdAt

        ) {
}
