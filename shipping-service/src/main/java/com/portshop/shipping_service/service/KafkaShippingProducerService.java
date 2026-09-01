package com.portshop.shipping_service.service;

import com.portshop.shipping_service.dto.ShipmentDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class KafkaShippingProducerService {

    private final KafkaTemplate<String, ShipmentDTO> kafkaPaymentTemplate;

    public KafkaShippingProducerService(
            KafkaTemplate<String, ShipmentDTO> kafkaTemplate
    ) {
        this.kafkaPaymentTemplate = kafkaTemplate;
    }


    public void publishForCreateShipment(ShipmentDTO shipmentDTO) {
        kafkaPaymentTemplate.send(
                "shipping-events",
                shipmentDTO
        );
    }

}
