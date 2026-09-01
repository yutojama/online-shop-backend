package com.portshop.payment_service.service;

import com.portshop.payment_service.dto.PaymentDTO;
import com.portshop.payment_service.exception.PaymentAuthenticationException;
import com.portshop.payment_service.exception.PaymentExistingException;
import com.portshop.payment_service.exception.PaymentNotFoundException;
import com.portshop.payment_service.model.Payment;
import com.portshop.payment_service.paymentEnum.PaymentStatus;
import com.portshop.payment_service.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentService {

    private final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * payment db services from here
     * */

    public PaymentDTO getPaymentDTO(Payment payment) {
        return PaymentDTO.builder()
                .paymentId(payment.getId().toString())
                .orderId(payment.getOrderId())
                .paymentStatus(payment.getPaymentStatus())
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .transactionId(payment.getTransactionId())
                .paidAt(payment.getPaidAt())
                .build();
    }

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public void checkExistingPayment(String orderId) {
        if(paymentRepository.existsPaymentByOrderId(orderId)){
            throw new PaymentExistingException("Payment with order id: " + orderId + " already exists");
        }
    }

    public Payment getPayment(String paymentId) {
        Payment payment = paymentRepository.getPaymentById(UUID.fromString(paymentId));
        if (payment == null) {
            throw new PaymentNotFoundException("Payment not found, paymentId: " + paymentId);
        }
        return payment;
    }


    /**
     * payment web service
     * */
    public void redirectToPaymentProvider(String paymentId) {
        logger.info("Redirecting to payment provider, paymentId: " + paymentId);
    }

    public void checkUserAuthentication(String paymentId, String kcId){
        Payment payment = getPayment(paymentId);
        if(!payment.getUserKeycloakId().equals(kcId)){
            throw new PaymentAuthenticationException("Payment with id: " + paymentId + " not your payment");
        }
    }

    public Payment pay(String paymentId) {

        Payment payment = getPayment(paymentId);

        // placeholder for payment finished successfully
        if (payment.getTransactionId()==null){
            payment.setTransactionId(UUID.randomUUID().toString());
        }
        payment.setPaidAt(Instant.now());
        payment.setPaymentStatus(PaymentStatus.PAID);
        Payment savedPayment = savePayment(payment);
        logger.info("Payment successful");
        return savedPayment;
    }

    public Payment cancel(String paymentId) {

        Payment payment = getPayment(paymentId);

        // placeholder for payment finished successfully
        if (payment.getTransactionId()==null){
            payment.setTransactionId(UUID.randomUUID().toString());
        }
        payment.setPaymentStatus(PaymentStatus.CANCELED);
        payment.setPaidAt(null);
        Payment savedPayment = savePayment(payment);
        logger.info("Payment cancelled");
        return savedPayment;
    }



}
