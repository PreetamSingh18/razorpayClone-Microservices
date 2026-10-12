package com.preesa.razorpay.common_lib.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(KafkaProperties.class) // To enable load of KafkaProperties Configuration properties file
public class SharedKafkaAutoConfiguration {


}
