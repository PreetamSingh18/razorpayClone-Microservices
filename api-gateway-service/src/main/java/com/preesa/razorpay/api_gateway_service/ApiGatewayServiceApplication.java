package com.preesa.razorpay.api_gateway_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.preesa.razorpay.api_gateway_service.security.SecurityRoutesProperties;

@SpringBootApplication
@EnableFeignClients 
@EnableConfigurationProperties (SecurityRoutesProperties.class)
public class ApiGatewayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayServiceApplication.class, args);
	}

}
