package com.portshop.payment_service.repository;

import com.portshop.payment_service.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Payment getPaymentById(UUID uuid);

    boolean existsPaymentByOrderId(String orderId);
}
