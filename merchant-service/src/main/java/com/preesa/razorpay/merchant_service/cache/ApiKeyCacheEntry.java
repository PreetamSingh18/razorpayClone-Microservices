package com.preesa.razorpay.merchant_service.cache;



import com.preesa.razorpay.common_lib.enums.Environment;

import java.time.Instant;
import java.util.UUID;

public record ApiKeyCacheEntry(
        UUID merchantId,
        String keyId,
        String keySecretHash,
        String previousKeySecretHash,
        Environment environment,
        boolean enabled,
        Instant gracePeriodExpiresAt

) {
    public boolean isInGracePeriod(){
        return gracePeriodExpiresAt != null && Instant.now().isBefore(gracePeriodExpiresAt);
    }
}
