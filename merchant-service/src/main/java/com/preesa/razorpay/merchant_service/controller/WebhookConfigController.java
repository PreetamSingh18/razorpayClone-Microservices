package com.preesa.razorpay.merchant_service.controller;


import com.preesa.razorpay.common_lib.context.MerchantContext;
import com.preesa.razorpay.merchant_service.dto.request.WebhookConfigRequest;
import com.preesa.razorpay.merchant_service.dto.response.WebhookConfigResponse;
import com.preesa.razorpay.merchant_service.service.WebhookConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/merchants/webhooks")
public class WebhookConfigController {

    private final WebhookConfigService webhookConfigService;
    private final MerchantContext merchantContext;

    @PostMapping("/create")
    public ResponseEntity<WebhookConfigResponse> create(@Valid @RequestBody WebhookConfigRequest request) {
        return ResponseEntity.ok(webhookConfigService.create(merchantContext.getMerchantId(), request));
    }

    @GetMapping("/getAllWebhook")
    public ResponseEntity<List<WebhookConfigResponse>> getAllWebhook() {
        return ResponseEntity.ok(webhookConfigService.getAll(merchantContext.getMerchantId()));
    }

    @GetMapping("/{configId}")
    public ResponseEntity<WebhookConfigResponse> getById(@PathVariable UUID configId) {
        return ResponseEntity.ok(webhookConfigService.getById(merchantContext.getMerchantId(), configId));
    }

    @PutMapping("/update/{configId}")
    public ResponseEntity<WebhookConfigResponse> update(@PathVariable UUID configId,
            @RequestBody WebhookConfigRequest request) {
        return ResponseEntity.ok(webhookConfigService.update(merchantContext.getMerchantId(), configId, request));
    }

    @DeleteMapping("/delete/{configId}")
    public ResponseEntity<Void> delete(@PathVariable UUID configId) {
        webhookConfigService.delete(merchantContext.getMerchantId(), configId);
        return ResponseEntity.noContent().build();
    }


}
