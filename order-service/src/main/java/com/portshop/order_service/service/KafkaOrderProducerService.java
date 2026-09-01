package com.portshop.order_service.service;

import com.portshop.order_service.dto.orderDTO.OrderDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class KafkaOrderProducerService {

    private final KafkaTemplate<String, OrderDTO> kafkaOrderSubmitTemplate;

    public KafkaOrderProducerService(
            KafkaTemplate<String, OrderDTO> kafkaTemplate
    ) {
        this.kafkaOrderSubmitTemplate = kafkaTemplate;
    }


    public void publishForOrderSubmit(OrderDTO orderDTO) {
        kafkaOrderSubmitTemplate.send(
                "order-submit-events",
                orderDTO
        );
    }

}
