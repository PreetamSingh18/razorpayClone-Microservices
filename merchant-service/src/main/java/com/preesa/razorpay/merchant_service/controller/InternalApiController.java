package com.preesa.razorpay.merchant_service.controller;

import java.util.Optional;
import java.util.UUID;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.preesa.razorpay.common_lib.cache.ApiKeyCacheEntry;
import com.preesa.razorpay.common_lib.exceptions.ResourceNotFoundException;
import com.preesa.razorpay.merchant_service.entity.ApiKey;
import com.preesa.razorpay.merchant_service.repository.ApiKeyRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/apikey")
public class InternalApiController {

      private final ApiKeyRepository apiKeyRepository;

    @GetMapping("/getApiKeyEntry/{keyId}")
    ApiKeyCacheEntry findByKeyId(@PathVariable String keyId){
      Optional<ApiKey> apiKey= apiKeyRepository.findById(UUID.fromString(keyId));
      if(apiKey.isEmpty() || apiKey== null){
        throw new ResourceNotFoundException("apiKey",keyId);
      }
      ApiKey res = apiKey.get();
      return new ApiKeyCacheEntry(res.getMerchant().getId(),res.getKeyId(),res.getKeySecretHash(),res.getPreviousKeySecretHash(),
    res.getEnvironment(),res.getEnabled(),res.getGracePeriodExpiresAt());
    }
}
