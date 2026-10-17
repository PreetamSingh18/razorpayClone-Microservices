package com.preesa.razorpay.api_gateway_service.security;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.preesa.razorpay.common_lib.exceptions.GatewayAuthenticationException;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component 
@RequiredArgsConstructor 
@Slf4j 
public class JwtAuthHandler {

    private final JwtVerifier jwtVerifier;

    public Map<String,String> authenticate(String jwtToken){
        Claims claims;

        try{
        claims = jwtVerifier.verifyAccessToken(jwtToken);
        }
        catch(Exception e){
            throw new GatewayAuthenticationException("API_GATEWAY_EXCEPTION",e.getMessage());
        }

        return Map.of(
            "X-Merchant-Id",jwtVerifier.extractMerchantId(claims),
            "X-Merchant-Role", jwtVerifier.extractRole(claims)
        );

    }

}
