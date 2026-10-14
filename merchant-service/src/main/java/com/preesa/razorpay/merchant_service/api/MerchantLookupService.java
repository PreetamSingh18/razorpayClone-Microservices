package com.preesa.razorpay.merchant_service.api;



import com.preesa.razorpay.common_lib.dto.SettlementBankDetails;
import com.preesa.razorpay.common_lib.dto.WebhookTarget;

import java.util.List;
import java.util.UUID;

public interface MerchantLookupService {
    
    List<WebhookTarget> getActiveConfigsForEvent(UUID merchantId, String event);

    List<UUID> getAllActiveMerchantIds();

    SettlementBankDetails getSettlementBankDetails(UUID merchantId);

}
