package com.preesa.razorpay.payment_service.gateway.adapter;


import com.preesa.razorpay.common_lib.dto.VaultChargeRequest;
import com.preesa.razorpay.payment_service.client.VaultServiceClient;
import com.preesa.razorpay.payment_service.gateway.PaymentAdapter;
import com.preesa.razorpay.payment_service.gateway.dto.PaymentRequest;
import com.preesa.razorpay.payment_service.gateway.dto.PaymentResult;
import com.preesa.razorpay.payment_service.processor.PaymentProcessorRouter;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import com.preesa.razorpay.common_lib.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class CardPaymentAdapter implements PaymentAdapter {
    private final PaymentProcessorRouter paymentProcessorRouter;
    private final VaultServiceClient vaultServiceClient;

    /**
     * @param request
     * @return
     */
    @Override
    @CircuitBreaker(name = "vault-service")
    @Retry(name = "vault-service")
    public PaymentResult initiate(PaymentRequest request) {
        try {
            log.info("Initiate Payment with CardPaymentAdapter with paymentId:{}", request.paymentId());

            String token = request.methodDetails().get("token").toString();
            PaymentProcessorResponse response = vaultServiceClient.vaultCharge(new VaultChargeRequest(request.paymentId(), token, request.amount(), request.methodDetails()));

            return switch (response) {
                case PaymentProcessorResponse.Pending pending ->
                        new PaymentResult.Pending(pending.processorReference());
                case PaymentProcessorResponse.Failure failure ->
                        new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
                case PaymentProcessorResponse.Success success ->
                        new PaymentResult.Success(success.transactionReference());
            };

        } catch (Exception e) {
            log.warn("Execption in Card payment adapter {}", e.getMessage());
            return new PaymentResult.Failure("CARD_PAYMENT_FAILURE", e.getMessage());
        }
    }

    /**
     * @param paymentId
     * @return
     */
    @Override
    public PaymentResult capture(UUID paymentId) {
        return new PaymentResult.Success("CARD_REF");
    }
}
