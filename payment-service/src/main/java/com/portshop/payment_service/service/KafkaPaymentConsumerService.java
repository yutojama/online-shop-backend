package com.portshop.payment_service.service;

import com.portshop.payment_service.dto.PaymentDTO;
import com.portshop.payment_service.dto.orderDTO.OrderDTO;
import com.portshop.payment_service.model.Payment;
import com.portshop.payment_service.paymentEnum.OrderStatus;
import com.portshop.payment_service.paymentEnum.PaymentStatus;
import com.portshop.payment_service.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KafkaPaymentConsumerService {

    private final Logger logger = LoggerFactory.getLogger(KafkaPaymentConsumerService.class);
    private final PaymentService paymentService;

    public KafkaPaymentConsumerService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(
            topics = "order-submit-events",
            groupId = "payment-service-group"
    )
    public PaymentDTO listenForPayment(OrderDTO orderDTO) {
        if(orderDTO.getOrderStatus().equals(OrderStatus.PENDING)){
            // idempotency key to prevent double payment
            paymentService.checkExistingPayment(orderDTO.getOrderId());

            Payment payment = Payment.builder()
                    .orderId(orderDTO.getOrderId())
                    .userKeycloakId(orderDTO.getUserKeycloakId())
                    .paymentStatus(orderDTO.getPaymentStatus())
                    .paymentMethod(orderDTO.getPaymentMethod())
                    .amount(orderDTO.getTotal())
                    .build();
            Payment savedPayment = paymentService.savePayment(payment);
            logger.info("Incoming payment, orderId: " + savedPayment.getOrderId());
            paymentService.redirectToPaymentProvider(savedPayment.getId().toString());
            return PaymentDTO.builder()
                    .paymentId(savedPayment.getId().toString())
                    .orderId(savedPayment.getOrderId())
                    .paymentStatus(savedPayment.getPaymentStatus())
                    .paymentMethod(savedPayment.getPaymentMethod())
                    .amount(savedPayment.getAmount())
                    .build();
        } else if (orderDTO.getOrderStatus().equals(OrderStatus.CANCELLED)) {
            Payment payment = paymentService.getPaymentByOrderId(orderDTO.getOrderId());
            payment.setPaymentStatus(PaymentStatus.CANCELED);
            paymentService.savePayment(payment);
            logger.info("Payment cancelled, orderId: " + payment.getOrderId());
        }
        return null;
    }

}
