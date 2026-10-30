package com.preesa.razorpay.payment_service.mapper;


import com.preesa.razorpay.payment_service.dto.response.PaymentResponse;
import com.preesa.razorpay.payment_service.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {

    @Mapping(target = "orderId" , source = "order.id")
    @Mapping(target = "paymentStatus" , source = "status")
    PaymentResponse toResponse(Payment payment);

    @Mapping(target = "orderId" , source = "order.id")
    @Mapping(target = "paymentStatus" , source = "status")
    List<PaymentResponse>toResponseList(List<Payment>payments);

}
