package com.preesa.razorpay.payment_service.entity;


import com.preesa.razorpay.common_lib.entity.BaseAuditEntity;
import com.preesa.razorpay.common_lib.enums.PaymentActor;
import com.preesa.razorpay.common_lib.enums.PaymentEvent;
import com.preesa.razorpay.common_lib.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_transition_log",
        indexes = {
                @Index(name = "idx_log_payment_id" ,columnList = "payment_id, event")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString 
public class PaymentTransitionLog  extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "from_status", nullable = false)
    private PaymentStatus fromStatus;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "to_status" , nullable = false)
    private PaymentStatus toStatus;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 50, nullable = false)
    private PaymentActor actor;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "event", nullable = false)
    private PaymentEvent event;

    Instant occurredAt;
}
