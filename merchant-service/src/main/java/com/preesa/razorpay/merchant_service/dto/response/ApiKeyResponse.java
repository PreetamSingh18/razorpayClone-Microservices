package com.preesa.razorpay.merchant_service.dto.response;



import com.preesa.razorpay.common_lib.enums.Environment;

import java.time.Instant;
import java.util.UUID;


public record ApiKeyResponse(
        UUID id,
        String keyId,
        Environment environment,
        boolean enabled,
        Instant lastUsedAt,
        Instant createdAt

) {
}
