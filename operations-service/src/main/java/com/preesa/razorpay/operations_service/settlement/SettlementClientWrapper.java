package com.preesa.razorpay.operations_service.settlement;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.preesa.razorpay.common_lib.dto.PaymentSettlementView;
import com.preesa.razorpay.common_lib.dto.SettlementBankDetails;
import com.preesa.razorpay.operations_service.client.MerchantServiceClient;
import com.preesa.razorpay.operations_service.client.PaymentServiceClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component 
@Slf4j 
@RequiredArgsConstructor 
public class SettlementClientWrapper {

    private final PaymentServiceClient paymentServiceClient;
    private final MerchantServiceClient merchantServiceClient;

    @CircuitBreaker (name = "payment-service")
    @Retry(name = "payment-service")
    List<PaymentSettlementView>findUnsettledCapturePayments(UUID merchantId){
        return paymentServiceClient.findUnsettledCapture(merchantId);
    }

    @CircuitBreaker (name = "payment-service")
    @Retry(name = "payment-service")
    void markSettledPayments(UUID paymentId){
         paymentServiceClient.markSettled(paymentId);
    }

    @CircuitBreaker (name = "merchant-service")
    @Retry(name = "merchant-service")
    SettlementBankDetails getSettlementBankDetailsFromMerchant(UUID merchantId){
        return merchantServiceClient.getSettlementBankDetails(merchantId);
    }
}
