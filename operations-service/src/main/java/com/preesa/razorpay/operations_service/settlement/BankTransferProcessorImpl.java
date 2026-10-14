package com.preesa.razorpay.operations_service.settlement;


import com.preesa.razorpay.common_lib.dto.SettlementBankDetails;
import com.preesa.razorpay.common_lib.entity.Money;
import com.preesa.razorpay.common_lib.util.RandomizerUtil;
import com.preesa.razorpay.operations_service.settlement.dto.BankTransferResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankTransferProcessorImpl implements BankTransferProcessor {

    @Override
    public BankTransferResult initiate(UUID settlementId, UUID merchantId, Money amount,
                                       SettlementBankDetails bankDetails) {
        // Call the Bank API

        String registrationRef = "TXN_" + RandomizerUtil.randomBase64(12);

        log.debug("Bank Transfer call completed for settlementId: {}, registrationRef: {}",
                settlementId, registrationRef);

        return new BankTransferResult(registrationRef);

    }

}
