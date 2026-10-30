package com.preesa.razorpay.vault_service.processor;


import com.preesa.razorpay.common_lib.dto.PaymentProcessorRequest;
import com.preesa.razorpay.common_lib.dto.PaymentProcessorResponse;
import com.preesa.razorpay.common_lib.util.RandomizerUtil;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CardPaymentProcessor {

    private  static final String PAN_CARD_DECLINED = "4000000000000002";
    private  static  final String PAN_CARD_EXPIRED ="4000000000000069";

    /**
     * @param request
     * @return
     */
    @Bulkhead(name = "vault-card-processor", type = Bulkhead.Type.THREADPOOL )
    public CompletableFuture<PaymentProcessorResponse> charge(PaymentProcessorRequest request) {
        String pan= request.pan();
        if (PAN_CARD_DECLINED.equals(pan)){
            log.warn("Pan card declined");
          return CompletableFuture.completedFuture(new  PaymentProcessorResponse.Failure("PAN_CARD_DECLINED","Pan card is declined"));
        }
        else if (PAN_CARD_EXPIRED.equals(pan)){
            log.warn("Pan card is expired");
            return CompletableFuture.completedFuture( new  PaymentProcessorResponse.Failure("PAN_CARD_EXPIRED","Pan card is expired"));
        }

        String processorRef = "CARD_PROCESSOR_"+ RandomizerUtil.randomBase64(24);

     return CompletableFuture.completedFuture(new PaymentProcessorResponse.Pending(processorRef));
    }


}
