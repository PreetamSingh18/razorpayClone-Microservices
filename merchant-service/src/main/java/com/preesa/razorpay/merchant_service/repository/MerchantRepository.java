package com.preesa.razorpay.merchant_service.repository;


import com.preesa.razorpay.common_lib.enums.MerchantStatus;
import com.preesa.razorpay.merchant_service.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MerchantRepository extends JpaRepository<Merchant, UUID> {

    boolean existsByEmail(String email);

    Object findByEmail(String email);

    List<UUID> findAllIdsByStatus(MerchantStatus active);
}

