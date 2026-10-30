package com.preesa.razorpay.merchant_service.service;



import com.preesa.razorpay.merchant_service.dto.request.WebhookConfigRequest;
import com.preesa.razorpay.merchant_service.dto.response.WebhookConfigResponse;

import java.util.List;
import java.util.UUID;

public interface WebhookConfigService {
    WebhookConfigResponse create(UUID merchantId, WebhookConfigRequest request);

    List<WebhookConfigResponse> getAll (UUID merchantId);

    WebhookConfigResponse getById (UUID merchantId, UUID configId);

    WebhookConfigResponse update(UUID merchantId, UUID configId, WebhookConfigRequest request);

    void delete (UUID merchantId,UUID configId);
}
