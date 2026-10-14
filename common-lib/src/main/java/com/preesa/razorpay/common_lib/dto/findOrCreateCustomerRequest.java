package com.preesa.razorpay.common_lib.dto;

import java.util.UUID;

public record findOrCreateCustomerRequest(
        UUID merchantId,
        String email,
        String name,
        String phone
) {
}
