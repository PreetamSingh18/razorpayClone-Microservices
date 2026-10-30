package com.preesa.razorpay.payment_service.strategy;


import com.preesa.razorpay.common_lib.util.RandomizerUtil;
import com.preesa.razorpay.payment_service.processor.PaymentProcessor;
import com.preesa.razorpay.common_lib.dto.PaymentProcessorRequest;
import com.preesa.razorpay.common_lib.dto.PaymentProcessorResponse;
import org.springframework.stereotype.Component;

@Component
public class NetBankingPaymentProcessor implements PaymentProcessor {
    /**
     * @param request
     * @return
     */
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request)  {

        final String bankFail= "BANK_FAIL";

        String BankName= request.methodDetails()!=null ? request.methodDetails().get("BANK").toString() : null;

        if(bankFail.equals(BankName)){
            return new PaymentProcessorResponse.Failure("NET_BANKING_PROCESSOR_REJECTED","Bank Rejected the NetBanking Request paymentId: "+ request.paymentId());

        }

        String processorRef = "NET_BANKING_REFERENCE_"+ RandomizerUtil.randomBase64(24);

       // String bankReference = "https://:redirect.com"+processorRef;

        return new PaymentProcessorResponse.Pending(processorRef);


    }

}
