package com.preesa.razorpay.payment_service.serviceImpl;


import com.preesa.razorpay.common_lib.enums.EventAggregateType;
import com.preesa.razorpay.common_lib.enums.OrderStatus;
import com.preesa.razorpay.common_lib.enums.PaymentEvent;
import com.preesa.razorpay.common_lib.enums.PaymentStatus;
import com.preesa.razorpay.common_lib.exceptions.BusinessRuleViolationException;
import com.preesa.razorpay.common_lib.exceptions.ResourceNotFoundException;
import com.preesa.razorpay.payment_service.dto.request.PaymentInitRequest;
import com.preesa.razorpay.payment_service.dto.response.PaymentResponse;
import com.preesa.razorpay.payment_service.entity.OrderRecord;
import com.preesa.razorpay.payment_service.entity.Payment;
import com.preesa.razorpay.payment_service.gateway.PaymentGatewayRouter;
import com.preesa.razorpay.payment_service.gateway.dto.PaymentRequest;
import com.preesa.razorpay.payment_service.gateway.dto.PaymentResult;
import com.preesa.razorpay.payment_service.mapper.PaymentMapper;
import com.preesa.razorpay.payment_service.outbox.OutBoxEventPublisher;
import com.preesa.razorpay.payment_service.processor.PaymentProcessorRouter;
import com.preesa.razorpay.payment_service.repository.OrderRepository;
import com.preesa.razorpay.payment_service.repository.PaymentRepository;
import com.preesa.razorpay.payment_service.saga.PaymentAuthorizationRecorder;
import com.preesa.razorpay.payment_service.service.PaymentService;
import com.preesa.razorpay.payment_service.statemachine.PaymentTransitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;

    private final PaymentRepository paymentRepository;

    private final PaymentGatewayRouter paymentGatewayRouter;

    private final PaymentMapper paymentMapper;

    private final PaymentProcessorRouter paymentProcessorRouter;

    private final PaymentTransitionService paymentTransitionService;

    private  final OutBoxEventPublisher eventPublisher;

    private final PaymentAuthorizationRecorder paymentAuthorizationRecorder;
    /**
     * @param merchantId
     * @param request
     * @return
     */
    @Override
    @Transactional
    public PaymentResponse initiate(UUID merchantId, PaymentInitRequest request) {
/*
        OrderRecord orderRecord = orderRepository.findByMerchantIdAndId(merchantId, request.orderId());
*/
       // Divided into two step of Transaction for SAGA orchestration.
       Payment payment = paymentAuthorizationRecorder.recordPayment(merchantId, request);

       // STEP 2: 

      PaymentResult result;
      try{
          result= paymentGatewayRouter.initiate(new PaymentRequest(payment.getId(),request.orderId(),merchantId,payment.getOrder().getAmount(),request.paymentMethod(),request.methodDetails()));
      }
      catch(Exception e){
           return paymentAuthorizationRecorder.compensateAuthorizationFailure(payment.getId(),"PAYMENT_GATEWAY_ROUTER_UNREACHABLE",e.getMessage());
      }

       return paymentAuthorizationRecorder.applyGatewayResult(payment.getId(),result);

    }

    /**
     * @param merchantId
     * @param paymentId
     * @return
     */
    @Override
    @Transactional 
    public PaymentResponse capture(UUID merchantId, UUID paymentId) {
//        Payment payment = paymentRepository.findByIdAndMerchantId(paymentId, merchantId).orElseThrow(() -> new ResourceNotFoundException("payment", paymentId));

        //PESSIMISTIC_WRITE LOCK
        Payment payment = paymentRepository.findByIdAndMerchantIdForUpdate(paymentId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("payment", paymentId));
        //payment.setStatus(PaymentStatus.CAPTURING);
        paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST);


     //   PaymentResult result = paymentProcessorRouter.capture(payment.getMethod(), paymentId);
          PaymentResult result = paymentGatewayRouter.capture(payment.getMethod(), paymentId);

        if (result instanceof PaymentResult.Success success) {
           // payment.setStatus(PaymentStatus.CAPTURED);
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
            payment.setCapturedAt(Instant.now());
            log.info("Payment Captured successfully for payment Id {}", paymentId);
        } else if (result instanceof PaymentResult.Failure(String errorCode, String errorDescription)) {
            //payment.setStatus(PaymentStatus.AUTHORIZED);
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAIL);
            payment.setErrorCode(errorCode);
            payment.setErrorDescription(errorDescription);
            log.warn("Payment Capture failed for payment Id {}", paymentId);
        }

        payment = paymentRepository.save(payment);

        // Store Events in OutBox to publish in Kafka

        eventPublisher.publish(EventAggregateType.PAYMENT,payment.getId(),"PAYMENT_STATUS_CHANGED",
                Map.of("orderId",payment.getOrder().getId().toString(),
                        "paymentId",payment.getId().toString(),
                        "merchantId",merchantId.toString(),
                        "paymentStatus",payment.getStatus().name(),
                        "amountUnits",payment.getAmount().getAmountUnits(),
                        "amountCurrency",payment.getAmount().getCurrency(),
                        "paymentMethod",payment.getMethod()

                ));


        return paymentMapper.toResponse(payment);
    }

    /**
     * @param id
     * @param isApproved
     * @param bankRef
     * @param errorCode
     * @param errorDesc
     */
    @Override
    @Transactional
    public void resolveAuthorization(UUID id, boolean isApproved, String bankRef, String errorCode, String errorDesc) {
//        Payment payment = paymentRepository.findById(id)
//                .orElseThrow(()->new ResourceNotFoundException("Payment",id));

        // PESSIMISTIC_WRITE LOCK
        Payment payment = paymentRepository.findByIdForUpdate(id)
                .orElseThrow(()->new ResourceNotFoundException("Payment",id));

        if(payment.getStatus()!= PaymentStatus.AUTHORIZING){
            log.warn("Invalid payment status change attempt while resolveAuthorization");
            throw new BusinessRuleViolationException("INVALID_PAYMENT_STATUS","Invalid payment status change attempt while resolveAuthorization");
        }

        OrderRecord orderRecord = payment.getOrder();

        if(isApproved){
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_SUCCESS);
            payment.setBankReference(bankRef);
            payment.setAuthorizedAt(Instant.now());

            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST);
            PaymentResult result = paymentGatewayRouter.capture(payment.getMethod(), id);

            if(result instanceof PaymentResult.Success){
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
                payment.setCapturedAt(Instant.now());
                orderRecord.setOrderStatus(OrderStatus.PAID);
            }
            else   if(result instanceof PaymentResult.Failure failure){
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAIL);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorCode(failure.errorDescription());
            }

        }
        else{
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
            payment.setErrorCode(errorCode);
            payment.setErrorDescription(errorDesc);
        }

    paymentRepository.save(payment);
    orderRepository.save(orderRecord);

        // Store Events in OutBox to publish in Kafka

        eventPublisher.publish(EventAggregateType.PAYMENT,payment.getId(),"PAYMENT_STATUS_CHANGED",
                Map.of("orderId",payment.getOrder().getId().toString(),
                        "paymentId",payment.getId().toString(),
                        "merchantId",payment.getMerchantId().toString(),
                        "paymentStatus",payment.getStatus().name(),
                        "amountUnits",payment.getAmount().getAmountUnits(),
                        "amountCurrency",payment.getAmount().getCurrency(),
                        "paymentMethod",payment.getMethod()

                ));

    }
}
