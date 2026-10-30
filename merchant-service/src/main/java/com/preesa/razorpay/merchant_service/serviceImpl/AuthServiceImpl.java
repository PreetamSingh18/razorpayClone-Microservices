package com.preesa.razorpay.merchant_service.serviceImpl;


import com.preesa.razorpay.common_lib.enums.UserRole;
import com.preesa.razorpay.common_lib.exceptions.BusinessRuleViolationException;
import com.preesa.razorpay.common_lib.exceptions.DuplicateResourceException;
import com.preesa.razorpay.common_lib.exceptions.ResourceNotFoundException;
import com.preesa.razorpay.merchant_service.dto.request.LoginRequest;
import com.preesa.razorpay.merchant_service.dto.request.MerchantSignUpRequest;
import com.preesa.razorpay.merchant_service.dto.response.LoginResponse;
import com.preesa.razorpay.merchant_service.dto.response.MerchantResponse;
import com.preesa.razorpay.merchant_service.entity.AppUser;
import com.preesa.razorpay.merchant_service.entity.Merchant;
import com.preesa.razorpay.merchant_service.mapper.MerchantMapper;
import com.preesa.razorpay.merchant_service.repository.AppUserRepository;
import com.preesa.razorpay.merchant_service.repository.MerchantRepository;
import com.preesa.razorpay.merchant_service.security.JwtUtill;
import com.preesa.razorpay.merchant_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final MerchantRepository merchantRepository;

    private final AppUserRepository appUserRepository;

    private final MerchantMapper merchantMapper;

    private  final JwtUtill jwtUtill ;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public MerchantResponse signUp(MerchantSignUpRequest merchantSignUpRequest) {
        if(merchantRepository.existsByEmail(merchantSignUpRequest.email())){
                  throw new DuplicateResourceException("DUPLICATE_MERCHANT_EMAIL","Merchant already exist with emailId :" +merchantSignUpRequest.email());
        }

//        Merchant merchant =Merchant.builder()
//                .email(merchantSignUpRequest.email())
//                .businessName(merchantSignUpRequest.businessName())
//                .businessType(merchantSignUpRequest.businessType())
//                .name(merchantSignUpRequest.name())
//                .build();
        Merchant merchant = merchantMapper.toEntityFromSignUpRequest(merchantSignUpRequest);

        merchant = merchantRepository.save(merchant);

        AppUser appUser = AppUser.builder()
                .email(merchantSignUpRequest.email())
                .merchant(merchant)
                .passwordHash(passwordEncoder.encode(merchantSignUpRequest.password()))
                .role(UserRole.OWNER)
                .build();

        appUserRepository.save(appUser);

//        return new MerchantResponse(merchant.getId(),merchant.getName(),merchant.getEmail(),merchant.getBusinessName(),merchant.getBusinessType(),merchant.getStatus());
          return merchantMapper.toResponse(merchant);
    }

    /**
     * @param loginRequest
     * @return
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // Validate user credentials
        // Note: Password validation should be done here if not using Spring Security's AuthenticationManager
        
        AppUser  appUser= appUserRepository.findByEmail(loginRequest.email()).orElseThrow(()->new ResourceNotFoundException("User",loginRequest.email()));

        if(!passwordEncoder.matches(loginRequest.password(),appUser.getPasswordHash())){
            throw new BusinessRuleViolationException("LOGIN_REQUEST", "Invalid password or incorrect password ");
        }
        
        String token=  jwtUtill.generateAccessToken(appUser.getEmail(),appUser.getMerchant().getId(),appUser.getRole().name());
        return new LoginResponse(token);
    }
}
