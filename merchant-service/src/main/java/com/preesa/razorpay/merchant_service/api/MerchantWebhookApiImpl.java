package com.preesa.razorpay.merchant_service.api;


import com.preesa.razorpay.common_lib.dto.SettlementBankDetails;
import com.preesa.razorpay.common_lib.dto.WebhookTarget;
import com.preesa.razorpay.common_lib.enums.MerchantStatus;

import com.preesa.razorpay.common_lib.util.SignerUtil;
import com.preesa.razorpay.merchant_service.entity.Merchant;
import com.preesa.razorpay.merchant_service.repository.MerchantRepository;
import com.preesa.razorpay.merchant_service.repository.WebhookConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;

import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service 
@RequiredArgsConstructor
@Slf4j 
public class MerchantWebhookApiImpl implements MerchantLookupService {

    private final WebhookConfigRepository webhookConfigRepository; 
    private final BytesEncryptor bytesEncryptor;
    private final MerchantRepository merchantRepository;
    
    @Override
	public List<WebhookTarget> getActiveConfigsForEvent(UUID merchantId, String event) {

        return webhookConfigRepository.findAllByMerchantIdAndEnabledTrue(merchantId).stream()
                .filter(config -> config.isSubscribedTo(event))
                .map(config -> {

                    byte[] cipherBytes = Base64.getDecoder().decode(config.getWebhookSecret());
                    byte[] decryptedSecretBytes = bytesEncryptor.decrypt(cipherBytes);
                    return new WebhookTarget(config.getId(), config.getTargetUrl(),
                            new String(decryptedSecretBytes, StandardCharsets.UTF_8));
                })
                .toList();

    }

    @Override
    public List<UUID> getAllActiveMerchantIds() {
        List <UUID>merchantIds = merchantRepository.findAllIdsByStatus(MerchantStatus.ACTIVE);
       
        log.info("Fectched all Active MerchantId :{}",merchantIds.toString());
        return  merchantIds;
    }

    @Override
    public SettlementBankDetails getSettlementBankDetails(UUID merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(
                () -> new ResourceNotFoundException("No Merchant found merchantId :" + merchantId));

        return new SettlementBankDetails(merchant.getSettlementBankAccount(),
                merchant.getSettlementBankIFSC(),
                merchant.getSettlementBankAccountHolderName());
    }

}
