package com.preesa.razorpay.operations_service.repository;


import com.preesa.razorpay.common_lib.enums.OutBoxStatus;

import com.preesa.razorpay.operations_service.entity.OutBoxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutBoxEvent, UUID> {

    List<OutBoxEvent> findByStatusOrderByCreatedAtAsc(OutBoxStatus status);
}
