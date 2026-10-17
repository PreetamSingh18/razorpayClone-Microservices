package com.preesa.razorpay.api_gateway_service.security;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class PublicRouteMatcher {

    private final SecurityRoutesProperties securityRoutesProperties;
   // private final AntPathMatcher antPathMatcher = new AntPathMatcher();

     public boolean isPublic(String path) {
        return securityRoutesProperties.getPublicRoutes().stream()
                .anyMatch(pattern -> new AntPathMatcher().match(pattern, path));
    }


}
