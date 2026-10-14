package com.preesa.razorpay.payment_service.entity;


import com.preesa.razorpay.common_lib.entity.BaseAuditEntity;
import com.preesa.razorpay.common_lib.entity.Money;
import com.preesa.razorpay.common_lib.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table (name = "order_record",
        indexes = {
                @Index(name = "idx_order_status" ,columnList = "merchant_id, order_status"),
                @Index(name = "idx_merchants_orders" ,columnList = "id, merchant_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString 
public class OrderRecord  extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID merchantId;

    @Column(name =  "customer_id")
    private UUID customerId;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.CREATED;

    @Embedded
    private Money amount;

    @Column(length = 200)
    private String receipt;

    @Column(nullable = false)
    @Builder.Default
    private int attempts=0;

    @JdbcTypeCode(value= SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String,Object> notes;

    private Instant expireAt;




}
