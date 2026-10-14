package com.preesa.razorpay.merchant_service.repository;


import com.preesa.razorpay.merchant_service.entity.MerchantWebhookConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WebhookConfigRepository extends JpaRepository<MerchantWebhookConfig,UUID> {
    
    List<MerchantWebhookConfig>findByMerchantId(UUID merchantId);

    Optional<MerchantWebhookConfig> findByIdAndMerchantId(UUID configId, UUID merchantId);

	List<MerchantWebhookConfig> findAllByMerchantIdAndEnabledTrue(UUID merchantId);
}
