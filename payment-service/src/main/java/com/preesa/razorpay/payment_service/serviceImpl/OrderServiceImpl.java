package com.preesa.razorpay.payment_service.serviceImpl;


import com.preesa.razorpay.common_lib.dto.findOrCreateCustomerRequest;
import com.preesa.razorpay.common_lib.enums.EventAggregateType;
import com.preesa.razorpay.common_lib.enums.OrderStatus;
import com.preesa.razorpay.common_lib.exceptions.BusinessRuleViolationException;
import com.preesa.razorpay.common_lib.exceptions.DuplicateResourceException;
import com.preesa.razorpay.common_lib.exceptions.ResourceNotFoundException;



import com.preesa.razorpay.payment_service.client.CustomerServiceClient;
import com.preesa.razorpay.payment_service.dto.request.CreateOrderRequest;
import com.preesa.razorpay.payment_service.dto.response.OrderResponse;
import com.preesa.razorpay.payment_service.dto.response.PaymentResponse;
import com.preesa.razorpay.payment_service.entity.OrderRecord;
import com.preesa.razorpay.payment_service.entity.Payment;
import com.preesa.razorpay.payment_service.mapper.OrderMapper;
import com.preesa.razorpay.payment_service.mapper.PaymentMapper;
import com.preesa.razorpay.payment_service.outbox.OutBoxEventPublisher;
import com.preesa.razorpay.payment_service.repository.OrderRepository;
import com.preesa.razorpay.payment_service.repository.PaymentRepository;
import com.preesa.razorpay.payment_service.service.OrderService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;
    private final OutBoxEventPublisher eventPublisher;
    private final CustomerServiceClient customerServiceClient;

    /**
     * @param merchantId
     * @return
     */
    @Override
    @Transactional
    @CircuitBreaker (name = "merchant-service")
    @Retry(name = "merchant-service")
    public OrderResponse createOrder(UUID merchantId, CreateOrderRequest request) {

        if(request.receipt() != null && orderRepository.existsByMerchantIdAndReceipt(merchantId,request.receipt())){
            throw new DuplicateResourceException("ORDER_RECEIPT_DUPLICATE", "Order already exists");
        }

        UUID customerId = null;
        if( request.customer()!= null){
            customerId = customerServiceClient.findOrCreateCustomer(new findOrCreateCustomerRequest(merchantId,
                    request.customer().email(),
                    request.customer().name(),
                    request.customer().phone())
            );
        }

        OrderRecord orderRecord = OrderRecord.builder()
                .merchantId(merchantId)
                .customerId(customerId)
                .amount(request.amount())
                .receipt(request.receipt())
                .notes(request.notes())
                .expireAt(request.expireAt()!=null ? request.expireAt(): Instant.now().plus(10, ChronoUnit.MINUTES))
                .build();

        orderRecord = orderRepository.save(orderRecord);

        // Store Events in OutBox to publish in Kafka

        eventPublisher.publish(EventAggregateType.ORDER,orderRecord.getId(),"ORDER_CREATED",
                Map.of("orderId",orderRecord.getId().toString(),
                        "merchantId",orderRecord.getMerchantId().toString(),
                        "orderStatus",orderRecord.getOrderStatus().name(),
                        "amountUnits",orderRecord.getAmount().getAmountUnits(),
                        "amountCurrency",orderRecord.getAmount().getCurrency()

                ));


        return orderMapper.toResponse(orderRecord);
//        return new OrderResponse(orderRecord.getId(),merchantId,orderRecord.getAmount(),orderRecord.getReceipt(),orderRecord.getNotes(),
//                orderRecord.getOrderStatus(),orderRecord.getAttempts(),orderRecord.getExpireAt(),orderRecord.getCreatedAt(),null);


    }

    /**
     * @param orderId
     * @return
     */
    @Override
    public OrderResponse getOrderById(UUID orderId, UUID merchantId) {
       OrderRecord orderRecords= orderRepository.findByMerchantIdAndId(merchantId,orderId);
        if(orderRecords == null){
              throw new ResourceNotFoundException("ORDER",orderId);
        }
        return orderMapper.toResponse(orderRecords);
//        return new OrderResponse(orderRecords.getId(),merchantId,orderRecords.getAmount(),orderRecords.getReceipt(),orderRecords.getNotes()
//                ,orderRecords.getOrderStatus(),orderRecords.getAttempts(),orderRecords.getExpireAt(),orderRecords.getCreatedAt(),null);
    }

    /**
     * @param orderId
     * @param merchantId
     * @return
     */
    @Override
    @Transactional
    public String cancelOrderById(UUID orderId, UUID merchantId) {
        OrderRecord orderRecords= orderRepository.findByMerchantIdAndId(merchantId,orderId);
        if(orderRecords == null){
            throw new ResourceNotFoundException("ORDER",orderId);
        }
        else if(orderRecords.getOrderStatus() == OrderStatus.CANCELLED || orderRecords.getOrderStatus() == OrderStatus.PAID){
            throw new BusinessRuleViolationException("ORDER_CANNOT_CANCEL","Order cannot be cancelled with status "+orderRecords.getOrderStatus().name());
        }
        orderRecords.setOrderStatus(OrderStatus.CANCELLED);
        orderRecords.setUpdatedAt(Instant.now());
        orderRecords.setUpdatedBy(merchantId.toString());
        orderRepository.save(orderRecords);

        // Store Events in OutBox to publish in Kafka

        eventPublisher.publish(EventAggregateType.ORDER,orderRecords.getId(),"ORDER_CANCELLED",
                Map.of("orderId",orderRecords.getId().toString(),
                        "merchantId",orderRecords.getMerchantId().toString(),
                        "orderStatus",orderRecords.getOrderStatus().name(),
                        "amountUnits",orderRecords.getAmount().getAmountUnits(),
                        "amountCurrency",orderRecords.getAmount().getCurrency()

                ));

        return "Order id : "+ orderId+" cancelled";
    }

    /**
     * @param orderId
     * @param merchantId
     * @return
     */
    @Override
    public List<PaymentResponse> getPaymentByOrderId(UUID orderId, UUID merchantId) {
        OrderRecord orderRecords= orderRepository.findByMerchantIdAndId(merchantId,orderId);
        List<Payment>paymentList = paymentRepository.findPaymentByOrderId(orderId);

        return paymentMapper.toResponseList(paymentList);
//        return paymentList.stream().map((payment)-> paymentMapper.toResponse(payment)).toList();
//        return new OrderResponse(orderRecords.getId(),merchantId,orderRecords.getAmount(),null,null
//                ,orderRecords.getOrderStatus(),orderRecords.getAttempts(),orderRecords.getExpireAt(),orderRecords.getCreatedAt(),paymentList);
    }
}
