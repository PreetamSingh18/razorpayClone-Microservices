package com.preesa.razorpay.common_lib.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GatewayAuthenticationException extends RuntimeException {
    private final  String errorCode;

    public GatewayAuthenticationException(String errorCode,  String message){
        super(message);
        this.errorCode=errorCode;
    }
}
