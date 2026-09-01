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
    @PostMapping("/pay/{paymentId}")
    public ResponseEntity<?> pay(
            @PathVariable String paymentId,
            Authentication authentication
    ) {
        // here should be a wait time
        paymentService.checkUserAuthentication(paymentId, authentication.getName());
        Payment payment = paymentService.pay(paymentId);
        PaymentDTO paymentDTO = paymentService.getPaymentDTO(payment);
        kafkaPaymentProducerService.publishForPayment(paymentDTO);
        // publish payment successful
        return ResponseEntity.ok().body(paymentDTO);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/cancel/{paymentId}")
    public ResponseEntity<?> cancel(
            @PathVariable String paymentId,
            Authentication authentication
    ) {
        // here should be a wait time
        paymentService.checkUserAuthentication(paymentId, authentication.getName());
        Payment payment = paymentService.cancel(paymentId);
        PaymentDTO paymentDTO = paymentService.getPaymentDTO(payment);
        kafkaPaymentProducerService.publishForPayment(paymentDTO);
        // publish payment successful
        return ResponseEntity.ok().body(paymentDTO);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/redirect/{paymentId}")
    public ResponseEntity<?> redirect(@PathVariable String paymentId, Authentication authentication) {
        paymentService.checkUserAuthentication(paymentId, authentication.getName());
        paymentService.redirectToPaymentProvider(paymentId);
        return ResponseEntity.ok().build();
    }

}
