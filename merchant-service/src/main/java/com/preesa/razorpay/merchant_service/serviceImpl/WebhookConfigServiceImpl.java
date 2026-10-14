package com.preesa.razorpay.merchant_service.serviceImpl;


import com.preesa.razorpay.common_lib.util.RandomizerUtil;
import com.preesa.razorpay.merchant_service.dto.request.WebhookConfigRequest;
import com.preesa.razorpay.merchant_service.dto.response.WebhookConfigResponse;
import com.preesa.razorpay.merchant_service.entity.Merchant;
import com.preesa.razorpay.merchant_service.entity.MerchantWebhookConfig;
import com.preesa.razorpay.merchant_service.mapper.WebhookConfigMapper;
import com.preesa.razorpay.merchant_service.repository.MerchantRepository;
import com.preesa.razorpay.merchant_service.repository.WebhookConfigRepository;
import com.preesa.razorpay.merchant_service.service.WebhookConfigService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WebhookConfigServiceImpl implements WebhookConfigService {

    private final WebhookConfigRepository webhookConfigRepository;
    private final WebhookConfigMapper configMapper;
    private final BytesEncryptor bytesEncryptor;
    private final MerchantRepository merchantRepository;

    @Override
    public WebhookConfigResponse create(UUID merchantId, WebhookConfigRequest request) {
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(
                () -> new ResourceNotFoundException("merchant",
                        "Merchant not found merchantId: " + merchantId.toString()));

        String rawSecret = RandomizerUtil.randomBase64(32);

        byte[] rawSecretByte = rawSecret.getBytes(StandardCharsets.UTF_8);

        byte[] encryptedSecretByte = bytesEncryptor.encrypt(rawSecretByte);

        String encryptedSecret = Base64.getEncoder().encodeToString(encryptedSecretByte);


        MerchantWebhookConfig config = MerchantWebhookConfig.builder()
                .eventTypes(request.eventTypes())
                .targetUrl(request.targetUrl())
                .webhookSecret(encryptedSecret)
                .merchant(merchant)
                .build();
        
        webhookConfigRepository.save(config);

        return configMapper.toResponse(config,rawSecret);
        
       
    }

    @Override
    public List<WebhookConfigResponse> getAll(UUID merchantId) {
        return webhookConfigRepository.findByMerchantId(merchantId).stream()
                .map(merchantWebhookConfig -> configMapper.toResponse(merchantWebhookConfig, null)).toList();
    }

    @Override
    public WebhookConfigResponse getById(UUID merchantId, UUID configId) {
        MerchantWebhookConfig config = getWebhookConfigFromDB(configId, merchantId);
        return configMapper.toResponse(config, null);

    }

    @Override
    @Transactional
    public WebhookConfigResponse update(UUID merchantId, UUID configId, WebhookConfigRequest request) {
        MerchantWebhookConfig config = getWebhookConfigFromDB(configId, merchantId);

        config.setTargetUrl(request.targetUrl());
        config.setEventTypes(request.eventTypes());
        config = webhookConfigRepository.save(config);
        return configMapper.toResponse(config, null);
    }

    @Override
    @Transactional
    public void delete(UUID merchantId, UUID configId) {
        MerchantWebhookConfig config = getWebhookConfigFromDB(configId, merchantId);

        webhookConfigRepository.delete(config);
    }

    private MerchantWebhookConfig getWebhookConfigFromDB(UUID configId, UUID merchantId) {
        MerchantWebhookConfig config = webhookConfigRepository.findByIdAndMerchantId(configId, merchantId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Merchant Webhook Config not found , merchantId " + merchantId));
        return config;
    }

}
