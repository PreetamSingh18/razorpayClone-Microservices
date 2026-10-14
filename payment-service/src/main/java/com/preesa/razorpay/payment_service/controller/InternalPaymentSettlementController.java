package com.preesa.razorpay.payment_service.controller;

import com.preesa.razorpay.common_lib.dto.PaymentSettlementView;
import com.preesa.razorpay.payment_service.api.PaymentLookupService;
import com.preesa.razorpay.payment_service.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/payments")
public class InternalPaymentSettlementController {

    private final PaymentLookupService paymentLookupService;

    @GetMapping("/unsettled-captured")
    List<PaymentSettlementView> findUnsettledCapture(@RequestParam UUID merchantId){
        List<Payment> unsettledCapturePayments = paymentLookupService.findUnsettledCapturePayments(merchantId);
        return unsettledCapturePayments
                .stream()
                .map(x -> new PaymentSettlementView(x.getId(),
                        Math.toIntExact(x.getAmount().getAmountUnits()),
                        0,
                        x.getAmount().getCurrency())).toList();

    };

    @PostMapping("/mark-settled")
    void markSettled(@RequestBody UUID paymentId){
        paymentLookupService.markPaymentAsSettled(paymentId);

    };
}
