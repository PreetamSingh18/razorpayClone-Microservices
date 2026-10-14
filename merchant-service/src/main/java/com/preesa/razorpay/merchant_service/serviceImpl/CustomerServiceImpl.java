package com.preesa.razorpay.merchant_service.serviceImpl;


import com.preesa.razorpay.common_lib.exceptions.ResourceNotFoundException;
import com.preesa.razorpay.merchant_service.entity.Customer;
import com.preesa.razorpay.merchant_service.entity.Merchant;
import com.preesa.razorpay.merchant_service.repository.CustomerRepository;
import com.preesa.razorpay.merchant_service.repository.MerchantRepository;
import com.preesa.razorpay.merchant_service.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

    private  final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;
    /**
     * @param merchantId
     * @param email
     * @param name
     * @param phone
     * @return
     */
    @Override
    @Transactional
    public UUID findOrCreate(UUID merchantId, String email, String name, String phone) {
       if( email== null || email.isBlank()){
           return null;
       }
       return customerRepository.findByMerchant_IdAndEmail(merchantId,email)
               .map(Customer::getId)
               .orElseGet(()->createNew(merchantId,email,name,phone));
    }

    private UUID createNew(UUID merchantId, String email, String name, String phone) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(()-> new ResourceNotFoundException("Merchant",merchantId));

        Customer customer= Customer.builder()
                .merchant(merchant)
                .email(email)
                .name(name)
                .contactNumber(phone)
                .build();

        customer= customerRepository.save(customer);

        log.info("Customer created via FindOrCreate id={} merchantId={} email={}",customer.getId(),merchantId,email);

        return customer.getId();
    }
}
