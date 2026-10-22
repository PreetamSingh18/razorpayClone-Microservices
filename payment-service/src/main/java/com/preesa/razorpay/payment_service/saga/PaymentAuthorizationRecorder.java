package com.preesa.razorpay.payment_service.saga;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
import com.preesa.razorpay.payment_service.gateway.dto.PaymentResult;
import com.preesa.razorpay.payment_service.mapper.PaymentMapper;
import com.preesa.razorpay.payment_service.outbox.OutBoxEventPublisher;
import com.preesa.razorpay.payment_service.repository.OrderRepository;
import com.preesa.razorpay.payment_service.repository.PaymentRepository;
import com.preesa.razorpay.payment_service.statemachine.PaymentTransitionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component 
@Slf4j 
@RequiredArgsConstructor 
public class PaymentAuthorizationRecorder {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentTransitionService paymentTransitionService;
      private  final OutBoxEventPublisher eventPublisher;
    private final PaymentMapper paymentMapper;

    @Transactional 
    public Payment recordPayment(UUID merchantId,PaymentInitRequest request, String idempotencyKey){
         //PESSIMISTIC_WRITE LOCK
        OrderRecord orderRecord = orderRepository.findByMerchantIdAndIdForUpdate(merchantId, request.orderId());

        if (orderRecord == null) {
            throw new ResourceNotFoundException("order", request.orderId());
        }

        if (orderRecord.getOrderStatus() != OrderStatus.CREATED && orderRecord.getOrderStatus() != OrderStatus.ATTEMPTED) {
            throw new BusinessRuleViolationException("ORDER_NOT_PAYABLE", "Order cannot be payable with status" + orderRecord.getOrderStatus());
        }
        orderRecord.setOrderStatus(OrderStatus.ATTEMPTED);
        orderRecord.setAttempts(orderRecord.getAttempts()+1);



        Payment payment= Payment.builder()
                .merchantId(merchantId)
                .order(orderRecord)
                .amount(orderRecord.getAmount())
                .status(PaymentStatus.CREATED)
                .idempotencyKey(idempotencyKey != null ? idempotencyKey: UUID.randomUUID().toString())
                .method(request.paymentMethod())
                .methodDetails(request.methodDetails())
                .build();

        payment = paymentRepository.save(payment);
        orderRepository.save(orderRecord);
        paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_ATTEMPT);

          return payment;
    }

    @Transactional 
    public PaymentResponse compensateAuthorizationFailure(UUID paymentId, String errorCode, String message) {
        
      Payment payment = paymentRepository.findById(paymentId).orElseThrow(
        ()->  new ResourceNotFoundException("payment", paymentId)
      );


      payment.setErrorCode(errorCode);
      payment.setErrorDescription(message);
      paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);

      payment= paymentRepository.save(payment);

      publishPaymentEvent(payment,"PAYMENT_AUTHORIZATION_COMPENSATED");
      

      return paymentMapper.toResponse(payment);

    }

    @Transactional 
    public PaymentResponse applyGatewayResult( UUID paymentId, PaymentResult result){
      log.info("Inside applyGatewayResult for paymentId {}", paymentId);
      
      Payment payment = paymentRepository.findById(paymentId).orElseThrow(
        ()->  new ResourceNotFoundException("payment", paymentId)
      );

      switch (result) {
            case PaymentResult.Pending(String processorRef) -> payment.setProcessorReference(processorRef);
            case PaymentResult.Failure(String errorCode, String errorDescription) -> {
                payment.setErrorCode(errorCode);
                payment.setErrorDescription(errorDescription);
              //  payment.setStatus(PaymentStatus.FAILED);
                paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
            }
            case PaymentResult.Success(String bankReference) -> {
                log.warn("Invalid State ");
                return null;
            }
        }
       
        payment = paymentRepository.save(payment);

        // Store Events in OutBox to publish in Kafka

        publishPaymentEvent(payment, "PAYMENT_CREATED");

        return paymentMapper.toResponse(payment);
    }

    public void publishPaymentEvent(Payment payment, String event) {
       
         eventPublisher.publish(EventAggregateType.PAYMENT,payment.getId(),event,
                Map.of("orderId",payment.getOrder().getId().toString(),
                        "paymentId",payment.getId().toString(),
                        "merchantId",payment.getMerchantId().toString(),
                        "paymentStatus",payment.getStatus().name(),
                        "amountUnits",payment.getOrder().getAmount().getAmountUnits(),
                        "amountCurrency",payment.getOrder().getAmount().getCurrency(),
                        "paymentMethod",payment.getMethod().name()

                ));

    }

    @Transactional(readOnly = true)
    public Optional<PaymentResponse> findExistingAttempts(UUID merchantId, String idempotencyKey) {
       return paymentRepository.findByMerchantIdAndIdempotencyKey(merchantId,idempotencyKey).map(paymentMapper::toResponse)  ;

    }

}
