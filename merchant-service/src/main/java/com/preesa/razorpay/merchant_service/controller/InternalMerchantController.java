package com.preesa.razorpay.merchant_service.controller;

import com.preesa.razorpay.common_lib.dto.SettlementBankDetails;
import com.preesa.razorpay.common_lib.dto.WebhookTarget;
import com.preesa.razorpay.merchant_service.api.MerchantLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/merchants")
public class InternalMerchantController {

    private MerchantLookupService merchantLookupService;

    @GetMapping("/active-ids")
    List<UUID> listActiveMerchantIds(){
        return merchantLookupService.getAllActiveMerchantIds();
    };

    @GetMapping("/{merchantId}/webhook-targets")
    List<WebhookTarget> getActiveConfigsForEvent(@PathVariable UUID merchantId, @RequestParam String eventType){
        return merchantLookupService.getActiveConfigsForEvent(merchantId,eventType);
    };

    @GetMapping("/{merchantId}/settlement-bank-details")
    SettlementBankDetails getSettlementBankDetails(@PathVariable UUID merchantId){
       return merchantLookupService.getSettlementBankDetails(merchantId);
    };
}
