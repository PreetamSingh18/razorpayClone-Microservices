package com.preesa.razorpay.operations_service.entity;

import com.preesa.razorpay.common_lib.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table( name = "dlq_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder 
@ToString 
public class DlqEvent  extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID merchantId;

    @JdbcTypeCode(value = SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String,Object> payload;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "webhook_event_id")
    private WebhookEvent webhookEvent;

    private String finalError;

    private Instant movedAt;

    private Instant replayedAt;

}
