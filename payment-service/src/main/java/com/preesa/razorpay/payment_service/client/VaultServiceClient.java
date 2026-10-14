package com.preesa.razorpay.payment_service.client;

import com.preesa.razorpay.common_lib.dto.PaymentProcessorResponse;
import com.preesa.razorpay.common_lib.dto.VaultChargeRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "vault-service", path = "/internal/vault")
public interface VaultServiceClient {

    @PostMapping("/charge")
    PaymentProcessorResponse vaultCharge(@RequestBody VaultChargeRequest request);
}


