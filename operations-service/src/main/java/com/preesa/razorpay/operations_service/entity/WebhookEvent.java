package com.preesa.razorpay.operations_service.entity;


import com.preesa.razorpay.common_lib.entity.BaseAuditEntity;
import com.preesa.razorpay.common_lib.enums.WebhookEventStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name ="webhook_event")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder 
@ToString 
public class WebhookEvent  extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID merchantId;

    @Column(nullable = false, length = 100)
    private String eventType;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WebhookEventStatus status;

    @JdbcTypeCode(value = SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String,Object> payload;

    @Column(nullable = false)
    private String targetUrl;

    @Column(nullable = false)
    private String signature;

    @Column(nullable = false)
    @Builder.Default
    private Integer attempts= 0;

    private Instant lastRetryAt;
    private Instant lastAttemptAt;

    private Integer lastResponseCode;

    @Column(length = 1000)
    private String lastResponseBody;  
    
    private Instant nextRetryAt;



    private Instant deliveredAt;
}
