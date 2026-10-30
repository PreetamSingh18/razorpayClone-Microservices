package com.preesa.razorpay.payment_service.strategy;


import com.preesa.razorpay.common_lib.util.RandomizerUtil;
import com.preesa.razorpay.payment_service.processor.PaymentProcessor;
import com.preesa.razorpay.common_lib.dto.PaymentProcessorRequest;
import com.preesa.razorpay.common_lib.dto.PaymentProcessorResponse;
import org.springframework.stereotype.Component;

@Component
public class UpiPaymentProcessor implements PaymentProcessor {
    /**
     * @param request
     * @return
     */
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {


        final String bankFailVpa= "bankfail@okaxis";

        String bankVpa= request.methodDetails()!=null ? request.methodDetails().get("BANK").toString() : null;

        if(bankFailVpa.equals(bankVpa)){
            return new PaymentProcessorResponse.Failure("UPI_PROCESSOR_REJECTED","Bank Rejected the UPI Request paymentId: "+ request.paymentId());

        }

        String processorRef = "UPI_REFERENCE_"+ RandomizerUtil.randomBase64(24);

      //  String bankReference = "BANK_REFERENCE_"+ RandomizerUtil.randomBase64(24);

        return new PaymentProcessorResponse.Pending(processorRef);


    }

}
