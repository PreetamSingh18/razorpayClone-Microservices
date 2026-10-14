package com.preesa.razorpay.payment_service.controller;

import com.preesa.razorpay.common_lib.context.MerchantContext;
import com.preesa.razorpay.payment_service.dto.request.PaymentInitRequest;
import com.preesa.razorpay.payment_service.dto.response.PaymentResponse;
import com.preesa.razorpay.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final MerchantContext merchantContext;



    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponse>initiate(@Valid @RequestBody PaymentInitRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.initiate(merchantContext.getMerchantId(),request));
    }
    @PostMapping("/{paymentId}/capture")
    public ResponseEntity<PaymentResponse>capture(@PathVariable UUID paymentId){
        return ResponseEntity.ok(paymentService.capture(merchantContext.getMerchantId(),paymentId));
    }


}
