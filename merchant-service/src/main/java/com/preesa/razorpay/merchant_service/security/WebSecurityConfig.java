package com.preesa.razorpay.merchant_service.security;


import com.preesa.razorpay.common_lib.idempotency.IdempotencyFilter;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final IdempotencyFilter idempotencyFilter;

    @Bean 
    public FilterRegistrationBean<Filter> idempotencyFilterRegistration(){
     FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>(idempotencyFilter);
     registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE+2);
     registrationBean.addUrlPatterns("/*");
     return  registrationBean;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


  
}
