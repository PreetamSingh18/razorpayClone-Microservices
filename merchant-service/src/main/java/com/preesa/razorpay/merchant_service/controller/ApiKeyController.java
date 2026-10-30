package com.preesa.razorpay.merchant_service.controller;



import com.preesa.razorpay.common_lib.context.MerchantContext;
import com.preesa.razorpay.merchant_service.dto.request.ApiKeyCreateRequest;
import com.preesa.razorpay.merchant_service.dto.response.ApiKeyCreateResponse;
import com.preesa.razorpay.merchant_service.dto.response.ApiKeyResponse;
import com.preesa.razorpay.merchant_service.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/api/merchants/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;
    private final MerchantContext merchantContext;

    @PostMapping("/create")
    public ResponseEntity<ApiKeyCreateResponse>create(@RequestBody @Valid ApiKeyCreateRequest apiKeyCreateRequest){
         UUID merchantId= merchantContext.getMerchantId();
           return ResponseEntity.status(HttpStatus.CREATED).body(apiKeyService.create(merchantId,apiKeyCreateRequest));
    }

    @GetMapping("/getAllApiKey")
    public ResponseEntity<List<ApiKeyResponse>>getListOfApiKey(){
        UUID merchantId= merchantContext.getMerchantId();
        return ResponseEntity.ok(apiKeyService.getListOfApiKey(merchantId));
    }

    @DeleteMapping("/revoke/{keyId}")
    public ResponseEntity<String>revoke(@PathVariable String keyId){
        UUID merchantId= merchantContext.getMerchantId();
        return ResponseEntity.ok(apiKeyService.revoke(merchantId,keyId));
    }

    @PostMapping("/rotate/{keyId}")
    public ResponseEntity<ApiKeyCreateResponse>rotateKey( @PathVariable String keyId){
        UUID merchantId= merchantContext.getMerchantId();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiKeyService.rotateKey(merchantId,keyId));
    }
}
