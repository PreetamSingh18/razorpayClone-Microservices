package com.preesa.razorpay.merchant_service.controller;

import com.preesa.razorpay.common_lib.dto.findOrCreateCustomerRequest;
import com.preesa.razorpay.merchant_service.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/internal/customers")
public class InternalCustomerController {

    private final CustomerService customerService;

    @PostMapping("/find-or-create")
    UUID findOrCreateCustomer(@RequestBody findOrCreateCustomerRequest request){
        return customerService.findOrCreate(request.merchantId(),request.email(),request.name(),request.phone());
    };
}
