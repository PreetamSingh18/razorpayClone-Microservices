package com.preesa.razorpay.vault_service.dto.response;


import com.preesa.razorpay.common_lib.enums.CardBrand;

public record TokenizerResponse(
        String token,
        CardBrand brand,
        String lastFour,
        Integer expiryMonth,
        Integer expiryYear

) {
}
