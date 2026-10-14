package com.preesa.razorpay.payment_service.api;


import com.preesa.razorpay.common_lib.enums.PaymentStatus;
import com.preesa.razorpay.common_lib.exceptions.ResourceNotFoundException;
import com.preesa.razorpay.payment_service.entity.Payment;
import com.preesa.razorpay.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service 
@RequiredArgsConstructor
@Slf4j  
public class PaymentLookupServiceImpl implements  PaymentLookupService {

    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public List<Payment> findUnsettledCapturePayments(UUID merchantId) {

        List<Payment> unsettledPayments = paymentRepository.findByMerchantIdAndStatusForUpdate(merchantId,
                PaymentStatus.CAPTURED);

        log.info("Total unsettled payment of MerchantId :{} is count :{}", merchantId, unsettledPayments.size());

        return unsettledPayments;

    }
    
    @Override
    @Transactional
    public void markPaymentAsSettled(UUID paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("PAYMENT", paymentId));

        payment.setStatus(PaymentStatus.SETTLED);
        payment.setSettledAt(Instant.now());

        paymentRepository.save(payment);
    }
    
}
