package com.preesa.razorpay.vault_service.service;


import com.preesa.razorpay.common_lib.dto.PaymentProcessorResponse;
import com.preesa.razorpay.common_lib.entity.Money;

import com.preesa.razorpay.vault_service.dto.request.TokenizerRequest;
import com.preesa.razorpay.vault_service.dto.response.TokenizerResponse;


import java.util.Map;
import java.util.UUID;

public interface VaultService {

    TokenizerResponse tokenizer(TokenizerRequest request, UUID merchantId);

    PaymentProcessorResponse charge(UUID paymentId, String token, Money amount, Map<String, Object> methodDetails);
}
