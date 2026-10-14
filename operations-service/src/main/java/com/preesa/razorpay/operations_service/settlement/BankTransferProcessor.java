package com.preesa.razorpay.operations_service.settlement;



import com.preesa.razorpay.common_lib.dto.SettlementBankDetails;
import com.preesa.razorpay.common_lib.entity.Money;
import com.preesa.razorpay.operations_service.settlement.dto.BankTransferResult;

import java.util.UUID;

public interface BankTransferProcessor {
    BankTransferResult initiate(UUID settlementId, UUID merchantId, Money amount, SettlementBankDetails bankDetails);

}
