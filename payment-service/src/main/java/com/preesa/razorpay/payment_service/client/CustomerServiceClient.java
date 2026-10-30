package com.preesa.razorpay.payment_service.client;

import com.preesa.razorpay.common_lib.dto.findOrCreateCustomerRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "merchant-service", path = "/internal/customers" , url = "${MERCHANT_SERVICE_URI:}")
public interface CustomerServiceClient {

    @PostMapping("/find-or-create")
    UUID findOrCreateCustomer(@RequestBody findOrCreateCustomerRequest request);
}
