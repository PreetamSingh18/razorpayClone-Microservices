package com.preesa.razorpay.merchant_service.service;


import com.preesa.razorpay.merchant_service.dto.request.LoginRequest;
import com.preesa.razorpay.merchant_service.dto.request.MerchantSignUpRequest;
import com.preesa.razorpay.merchant_service.dto.response.LoginResponse;
import com.preesa.razorpay.merchant_service.dto.response.MerchantResponse;
import jakarta.validation.Valid;

public interface AuthService {
    public MerchantResponse signUp(MerchantSignUpRequest merchantSignUpRequest);

    LoginResponse login(@Valid LoginRequest loginRequest);
}
