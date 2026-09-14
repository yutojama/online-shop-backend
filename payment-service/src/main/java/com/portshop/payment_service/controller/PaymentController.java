package com.portshop.payment_service.controller;


import com.portshop.payment_service.dto.PaymentDTO;
import com.portshop.payment_service.model.Payment;
import com.portshop.payment_service.service.KafkaPaymentProducerService;
import com.portshop.payment_service.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final KafkaPaymentProducerService kafkaPaymentProducerService;

    public PaymentController(PaymentService paymentService, KafkaPaymentProducerService kafkaPaymentProducerService) {
        this.paymentService = paymentService;
        this.kafkaPaymentProducerService = kafkaPaymentProducerService;
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/pay/{orderId}")
    public ResponseEntity<?> pay(
            @PathVariable String orderId,
            Authentication authentication
    ) {
        // here should be a wait time
        paymentService.checkUserAuthentication(orderId, authentication.getName());
        Payment payment = paymentService.pay(orderId);
        PaymentDTO paymentDTO = paymentService.getPaymentDTO(payment);
        kafkaPaymentProducerService.publishForPayment(paymentDTO);
        // publish payment successful
        return ResponseEntity.ok().body(paymentDTO);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<?> cancel(
            @PathVariable String orderId,
            Authentication authentication
    ) {
        // here should be a wait time
        paymentService.checkUserAuthentication(orderId, authentication.getName());
        Payment payment = paymentService.cancel(orderId);
        PaymentDTO paymentDTO = paymentService.getPaymentDTO(payment);
        kafkaPaymentProducerService.publishForPayment(paymentDTO);
        // publish payment successful
        return ResponseEntity.ok().body(paymentDTO);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/redirect/{orderId}")
    public ResponseEntity<?> redirect(@PathVariable String orderId, Authentication authentication) {
        paymentService.checkUserAuthentication(orderId, authentication.getName());
        paymentService.redirectToPaymentProvider(orderId);
        return ResponseEntity.ok().build();
    }

}
