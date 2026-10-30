package com.preesa.razorpay.payment_service.gateway.adapter;


import com.preesa.razorpay.common_lib.enums.PaymentMethod;
import com.preesa.razorpay.payment_service.gateway.PaymentAdapter;
import com.preesa.razorpay.payment_service.gateway.dto.PaymentRequest;
import com.preesa.razorpay.payment_service.gateway.dto.PaymentResult;
import com.preesa.razorpay.payment_service.processor.PaymentProcessorRouter;
import com.preesa.razorpay.common_lib.dto.PaymentProcessorRequest;
import com.preesa.razorpay.common_lib.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
@Slf4j
@RequiredArgsConstructor
public class UpiPaymentAdapter implements PaymentAdapter {

    private final PaymentProcessorRouter paymentProcessorRouter;

    /**
     * @param request
     * @return
     */
    @Override
    public PaymentResult initiate(PaymentRequest request) {
        try {
            log.info("Initiate Payment with UpiPaymentAdapter with paymentId:{}", request.paymentId());

            PaymentProcessorRequest paymentProcessorRequest = PaymentProcessorRequest.noncard(request.paymentId(), PaymentMethod.UPI, request.amount(), request.methodDetails());

            PaymentProcessorResponse response = paymentProcessorRouter.charge(paymentProcessorRequest);

            return switch (response) {
                case PaymentProcessorResponse.Pending pending ->
                        new PaymentResult.Pending(pending.processorReference());
                case PaymentProcessorResponse.Failure failure ->
                        new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
                case PaymentProcessorResponse.Success success ->
                        new PaymentResult.Success(success.transactionReference());
            };

        } catch (Exception e) {
            log.warn("Execption in UPI payment adapter {}", e.getMessage());
            return new PaymentResult.Failure("UPI_FAILURE", e.getMessage());
        }

    }

    /**
     * @param paymentId
     * @return
     */
    @Override
    public PaymentResult capture(UUID paymentId) {
        return new PaymentResult.Success("UPI_REF");
    }
}
