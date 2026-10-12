package com.preesa.razorpay.common_lib.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdempotencyConflictException extends RuntimeException {


    public IdempotencyConflictException(String message){
        super(message);
    }
}
