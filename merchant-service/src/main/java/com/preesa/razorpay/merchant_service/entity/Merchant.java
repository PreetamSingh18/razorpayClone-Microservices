package com.preesa.razorpay.merchant_service.entity;



import com.preesa.razorpay.common_lib.entity.BaseAuditEntity;
import com.preesa.razorpay.common_lib.enums.BusinessType;
import com.preesa.razorpay.common_lib.enums.MerchantStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "merchant",
        indexes = {
                @Index(name = "idx_merchant_status" ,columnList = "status")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString 
public class Merchant  extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 15)
    private String contactNumber;

    @Column(length = 200)
    private String websiteUrl;

    @Column(length = 50)
    private String businessName;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 50)
    private BusinessType businessType;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 50, nullable = false)
    @Builder.Default
    private MerchantStatus status = MerchantStatus.PENDING_KYC;

    private String gstId;

    @Column(length = 20)
    private String panId;

    @Column(length = 200)
    private String settlementBankAccount;

    @Column(length = 20)
    private String settlementBankIFSC;

    @Column(length = 200)
    private String settlementBankAccountHolderName;


}
