package com.preesa.razorpay.common_lib.web;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.filter.RequestContextFilter;

import com.preesa.razorpay.common_lib.context.MerchantContext;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;

@AutoConfiguration 
@RequiredArgsConstructor 
public class SharedWebFilterAutoConfiguration {

    @Bean 
    @ConditionalOnProperty(name = "app.security.trust-inbound-headers", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<Filter> merchantContextFilterRegistration(MerchantContext merchantContext){
     FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>(new MerchantContextFilter(merchantContext));
     registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE+1);
     registrationBean.addUrlPatterns("/*");
     return  registrationBean;
    }


    @Bean
    public FilterRegistrationBean<Filter> requestContextFilterRegistration() {
        FilterRegistrationBean<Filter> registration =
                new FilterRegistrationBean<>(new RequestContextFilter());
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.addUrlPatterns("/*");
        return registration;
    }

}
