package com.preesa.razorpay.payment_service.config;


import com.preesa.razorpay.common_lib.enums.PaymentMethod;
import com.preesa.razorpay.payment_service.processor.PaymentProcessor;
import com.preesa.razorpay.payment_service.strategy.CardPaymentProcessor;
import com.preesa.razorpay.payment_service.strategy.NetBankingPaymentProcessor;
import com.preesa.razorpay.payment_service.strategy.UpiPaymentProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentProcessorConfig {

    private final CardPaymentProcessor cardPaymentProcessor;
    private final NetBankingPaymentProcessor netBankingPaymentProcessor;
    private final UpiPaymentProcessor upiPaymentProcessor;

    @Bean
    public Map<PaymentMethod, PaymentProcessor> paymentProcessorMap() {
        return Map.of(
                PaymentMethod.CARD, cardPaymentProcessor,
                PaymentMethod.NETBANKING, netBankingPaymentProcessor,
                PaymentMethod.UPI, upiPaymentProcessor
        );
    }
}
