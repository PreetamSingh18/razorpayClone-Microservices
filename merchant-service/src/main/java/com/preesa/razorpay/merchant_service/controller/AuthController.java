package com.preesa.razorpay.merchant_service.controller;



import com.preesa.razorpay.merchant_service.dto.request.LoginRequest;
import com.preesa.razorpay.merchant_service.dto.request.MerchantSignUpRequest;
import com.preesa.razorpay.merchant_service.dto.response.LoginResponse;
import com.preesa.razorpay.merchant_service.dto.response.MerchantResponse;
import com.preesa.razorpay.merchant_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("v1/auth")
@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<MerchantResponse>signUp(@RequestBody @Valid MerchantSignUpRequest merchantSignUpRequest){
             return ResponseEntity.status(HttpStatus.CREATED).body(
                     authService.signUp(merchantSignUpRequest)
             );
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse>login(@RequestBody @Valid LoginRequest loginRequest){
        return ResponseEntity.status(HttpStatus.OK).body(
                authService.login(loginRequest)
        );
    }

}
