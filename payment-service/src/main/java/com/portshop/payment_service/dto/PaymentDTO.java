package com.portshop.payment_service.dto;

import com.portshop.payment_service.paymentEnum.OrderStatus;
import com.portshop.payment_service.paymentEnum.PaymentMethod;
import com.portshop.payment_service.paymentEnum.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {

    private String paymentId;
    private String orderId;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private String transactionId;
    private Instant paidAt;

}
