package com.portshop.shipping_service.dto;

import com.portshop.shipping_service.shippingEnum.PaymentMethod;
import com.portshop.shipping_service.shippingEnum.PaymentStatus;
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
