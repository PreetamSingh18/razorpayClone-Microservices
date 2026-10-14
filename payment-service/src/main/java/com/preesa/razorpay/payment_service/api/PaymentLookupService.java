package com.preesa.razorpay.payment_service.api;



import com.preesa.razorpay.payment_service.entity.Payment;

import java.util.List;
import java.util.UUID;

public interface PaymentLookupService {

    public List<Payment> findUnsettledCapturePayments(UUID merchantId);
    void markPaymentAsSettled(List<UUID> paymentIds);
    
}
