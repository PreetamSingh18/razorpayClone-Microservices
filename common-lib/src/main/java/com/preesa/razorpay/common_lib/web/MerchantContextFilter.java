package com.preesa.razorpay.common_lib.web;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.preesa.razorpay.common_lib.context.MerchantContext;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor 
public class MerchantContextFilter extends OncePerRequestFilter {

    private static final String MERCHANT_ID_HEADER ="X-Merchant-Id";
    private static final String KEY_ID_HEADER ="X-Key-Id";
    private final MerchantContext merchantContext;

      
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
       
        String merchantId = request.getHeader(MERCHANT_ID_HEADER);
        if(merchantId != null && !merchantId.isBlank()){
            merchantContext.setMerchantId(UUID.fromString(merchantId));
        }  
        
        String keyId = request.getHeader(KEY_ID_HEADER);
        if(keyId != null && !keyId.isBlank()){
            merchantContext.setKeyId(keyId);
        }  

        filterChain.doFilter(request, response);
    }

}
