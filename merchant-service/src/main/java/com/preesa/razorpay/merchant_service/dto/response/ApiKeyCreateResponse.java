package com.preesa.razorpay.merchant_service.dto.response;



import com.preesa.razorpay.common_lib.enums.Environment;

import java.util.UUID;

public record ApiKeyCreateResponse(
        UUID id,
        String keyId,
        String keySecret,
        Environment environment

) {
}
