package com.preesa.razorpay.operations_service.repository;


import com.preesa.razorpay.operations_service.entity.SettlementPayment;
import com.preesa.razorpay.operations_service.entity.SettlementPaymentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SettlementPaymentRepository extends JpaRepository<SettlementPayment, SettlementPaymentId> {
    
    SettlementPayment findBySettlementId(UUID id);

}
