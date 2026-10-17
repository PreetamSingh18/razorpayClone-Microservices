package com.preesa.razorpay.api_gateway_service.security;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties (prefix = "app.security")
@Getter 
@Setter 
public class SecurityRoutesProperties {

    public List<String> publicRoutes = List.of();

}
