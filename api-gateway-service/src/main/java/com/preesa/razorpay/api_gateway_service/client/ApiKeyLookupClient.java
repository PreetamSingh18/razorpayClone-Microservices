package com.preesa.razorpay.api_gateway_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.preesa.razorpay.common_lib.cache.ApiKeyCacheEntry;

@FeignClient (name = "merchant-service", path = "/internal/apikey", url = "${MERCHANT_SERVICE_URI:}")
public interface ApiKeyLookupClient {


    @GetMapping("/getApiKeyEntry/{keyId}")
    ApiKeyCacheEntry findByKeyId(@PathVariable String keyId);

}
