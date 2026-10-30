package com.preesa.razorpay.operations_service.settlement;


import com.preesa.razorpay.common_lib.enums.SettlementStatus;
import com.preesa.razorpay.operations_service.entity.Settlement;
import com.preesa.razorpay.operations_service.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j 
@Component
@RequiredArgsConstructor 
public class BankSettlementCallbackSimulator {

    private final SettlementRepository settlementRepository;
    private final SettlementTransactionExecutor settlementTransactionExecutor;

    @Scheduled (fixedDelayString = "5000")
    public void  processCallBacks(){

        List<Settlement> settlementsPending = settlementRepository.findByStatus(SettlementStatus.TRANSFER_PENDING);

        if(settlementsPending  == null){
        log.info("No Pending Settlements records found to Process");
        return;
        }

        for(Settlement settlement : settlementsPending){
            simulateCallback(settlement);
        }


    }

    private void simulateCallback(Settlement settlement) {
       log.info("Initiating Settlement callback for settlement id:{} ", settlement.getId());
       settlementTransactionExecutor.resolveTransfer(settlement.getId(),null, null);
    }
    
}
