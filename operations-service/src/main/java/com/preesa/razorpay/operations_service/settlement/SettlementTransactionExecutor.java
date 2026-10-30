package com.preesa.razorpay.operations_service.settlement;


import com.preesa.razorpay.common_lib.dto.PaymentSettlementView;
import com.preesa.razorpay.common_lib.dto.SettlementBankDetails;
import com.preesa.razorpay.common_lib.entity.Money;
import com.preesa.razorpay.common_lib.enums.EventAggregateType;
import com.preesa.razorpay.common_lib.enums.SettlementStatus;
import com.preesa.razorpay.common_lib.exceptions.ResourceNotFoundException;
import com.preesa.razorpay.operations_service.client.MerchantServiceClient;
import com.preesa.razorpay.operations_service.client.PaymentServiceClient;
import com.preesa.razorpay.operations_service.entity.Settlement;
import com.preesa.razorpay.operations_service.entity.SettlementPayment;
import com.preesa.razorpay.operations_service.entity.SettlementPaymentId;
import com.preesa.razorpay.operations_service.outbox.OutBoxEventPublisher;
import com.preesa.razorpay.operations_service.repository.SettlementPaymentRepository;
import com.preesa.razorpay.operations_service.repository.SettlementRepository;
import com.preesa.razorpay.operations_service.settlement.dto.BankTransferResult;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementTransactionExecutor {
    private static final double FEE_RATE = 0.02;
    private static final double GST_RATE = 0.18;


    private final SettlementRepository settlementRepository;
    private final SettlementPaymentRepository settlementPaymentRepository;
    private final BankTransferProcessor bankTransferProcessor;
    private final OutBoxEventPublisher outBoxEventPublisher;
    private final SettlementClientWrapper settlementClientWrapper;


    @Transactional
    public void processForMerchant(UUID merchantId, Instant date) {

        LocalDate localDate = date.atZone(ZoneId.systemDefault()).toLocalDate();

        List<PaymentSettlementView> payments = settlementClientWrapper.findUnsettledCapturePayments(merchantId);

        if (payments == null || payments.isEmpty()) {
            log.info("No unsettled Payment found to settle for merchantId :{} at {}", merchantId, localDate);
            return;
        }

        Long gross = Long.valueOf(payments.stream().map((x) -> x.amountUnits()).reduce(Integer::sum ).orElse(0));

        Money grossAmt= Money.of(gross, payments.getFirst().currency());

        long fee = Math.round(grossAmt.getAmountUnits() * FEE_RATE);
        long gst = Math.round(fee * GST_RATE);
        Money feeAmount = Money.of(fee, grossAmt.getCurrency());
        Money gstAmount = Money.of(gst, grossAmt.getCurrency());
        Money netAmount = grossAmt.subtract(feeAmount).subtract(gstAmount);

        Settlement settlement = Settlement.builder()
                .feeAmount(feeAmount)
                .gstAmount(gstAmount)
                .grossAmount(grossAmt)
                .netAmount(netAmount)
                .merchantId(merchantId)
                .status(SettlementStatus.INITIATED)
                .build();

        settlement = settlementRepository.save(settlement);

        try {
            List<SettlementPayment> settlementPayments = new ArrayList<>();
            for (PaymentSettlementView payment : payments) {
                settlementPayments.add(
                        SettlementPayment.builder().id(new SettlementPaymentId(payment.paymentId(), settlement.getId()))
                                .settlement(settlement).build());
            }

            settlementPaymentRepository.saveAll(settlementPayments);



            SettlementBankDetails bankDetails = settlementClientWrapper.getSettlementBankDetailsFromMerchant(merchantId);

            // Calling BankTransfer Processor.
            BankTransferResult bankTransferResult = bankTransferProcessor.initiate(settlement.getId(), merchantId,
                    netAmount, bankDetails);

            settlement.setStatus(SettlementStatus.TRANSFER_PENDING);
            settlement.setBankReference(bankTransferResult.bankReference());

            settlementRepository.save(settlement);
        } catch (Exception e) {
            log.error("Settlement Failed for settlementId :{} on date :{}", settlement, localDate);
            settlement.setStatus(SettlementStatus.FAILED);

            settlementRepository.save(settlement);

        }

    }



    @Transactional
    public void resolveTransfer(UUID id, String errorCode, String errorDescription) {
        Settlement settlement = settlementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SETTLEMENT", id));

        if (!settlement.getStatus().equals(SettlementStatus.TRANSFER_PENDING)) {
            log.info("This settlement is not eligible for process, settlementId :{}", id);
            return;
        }

        if (errorCode == null) {
            settlement.setStatus(SettlementStatus.PROCESSED);
            settlement.setSettledAt(Instant.now());



            SettlementPayment settlementPayment = settlementPaymentRepository.findBySettlementId(settlement.getId());

            settlementClientWrapper.markSettledPayments(settlementPayment.getId().getPaymentId());

            settlementRepository.save(settlement);

            log.info("Settlement processsed Successfully , settlementId :{}", id);
            outBoxEventPublisher.publish(EventAggregateType.SETTLEMENT, id, "SETTLEMENT_PROCESSED", Map.of(
                    "settlementId", id,
                    "merchantId", settlement.getMerchantId(),
                    "settlementStatus", settlement.getStatus().name(),
                    "settlementCurrency", settlement.getNetAmount().getCurrency(),
                    "settlementAmount", settlement.getNetAmount().getAmountUnits()

            ));

        } else {
            settlement.setStatus(SettlementStatus.FAILED);
            settlement.setFailureReason(errorCode + " : " + errorDescription);

            settlementRepository.save(settlement);
            log.warn("Settlement failed to process, settlementId :{}", id);

            outBoxEventPublisher.publish(EventAggregateType.SETTLEMENT, id, "SETTLEMENT_FAILED", Map.of(
                    "settlementId", id,
                    "merchantId", settlement.getMerchantId(),
                    "settlementStatus", settlement.getStatus().name(),
                    "settlementCurrency", settlement.getNetAmount().getCurrency(),
                    "settlementAmount", settlement.getNetAmount().getAmountUnits()

            ));

        }

    }
}