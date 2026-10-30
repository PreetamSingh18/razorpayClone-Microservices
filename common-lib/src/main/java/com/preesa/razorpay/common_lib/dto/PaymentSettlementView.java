package com.preesa.razorpay.common_lib.dto;

import java.util.UUID;

public record PaymentSettlementView(
        UUID paymentId,
        int amountUnits,
        int refundAmountUnits,
        String currency
) {
}
