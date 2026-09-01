package com.portshop.payment_service.service;

import com.portshop.payment_service.dto.PaymentDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class KafkaPaymentProducerService {

    private final KafkaTemplate<String, PaymentDTO> kafkaPaymentTemplate;

    public KafkaPaymentProducerService(
            KafkaTemplate<String, PaymentDTO> kafkaTemplate
    ) {
        this.kafkaPaymentTemplate = kafkaTemplate;
    }


    public void publishForPayment(PaymentDTO paymentDTO) {
        kafkaPaymentTemplate.send(
                "payment-events",
                paymentDTO
        );
    }

}
