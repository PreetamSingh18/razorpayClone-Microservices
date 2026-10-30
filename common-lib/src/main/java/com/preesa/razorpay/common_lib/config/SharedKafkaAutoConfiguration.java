package com.preesa.razorpay.common_lib.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties(KafkaProperties.class) // To enable load of KafkaProperties Configuration properties file
public class SharedKafkaAutoConfiguration {


}
