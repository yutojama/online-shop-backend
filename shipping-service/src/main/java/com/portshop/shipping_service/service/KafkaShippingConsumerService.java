package com.portshop.shipping_service.service;

import com.portshop.shipping_service.dto.PaymentDTO;
import com.portshop.shipping_service.dto.ShipmentDTO;
import com.portshop.shipping_service.dto.orderDTO.OrderDTO;
import com.portshop.shipping_service.model.Shipment;
import com.portshop.shipping_service.shippingEnum.PaymentStatus;
import com.portshop.shipping_service.shippingEnum.ShipmentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class KafkaShippingConsumerService {

    private final Logger logger = LoggerFactory.getLogger(KafkaShippingConsumerService.class);
    private final ShippingService shippingService;
    private final KafkaShippingProducerService kafkaShippingProducerService;

    public KafkaShippingConsumerService(ShippingService shippingService, KafkaShippingProducerService kafkaShippingProducerService) {
        this.shippingService = shippingService;
        this.kafkaShippingProducerService = kafkaShippingProducerService;
    }


    @KafkaListener(
            topics = "order-submit-events",
            groupId = "shipping-service-group"
    )
    @Transactional
    public void listenForOrder(OrderDTO orderDTO) {
        // idempotency key to prevent double payment
        shippingService.checkExistingShipment(orderDTO.getOrderId());
        List<ShipmentDTO> shipmentDTOList = new ArrayList<>();

        orderDTO.getOrderShipmentDTOList().forEach(orderShipment -> {
            Shipment shipment = Shipment.builder()
                    .orderId(orderDTO.getOrderId())
                    .shopKeycloakId(orderShipment.getShopKeycloakId())
                    .orderShipmentId(orderShipment.getShipmentId())
                    .carrier(orderShipment.getCarrier())
                    .shipmentStatus(ShipmentStatus.PENDING)
                    .trackingNumber(UUID.randomUUID().toString())
                    .build();
            Shipment savedShipment = shippingService.saveShipment(shipment);
            shipmentDTOList.add(
                    ShipmentDTO.builder()
                            .shipmentId(savedShipment.getOrderShipmentId())
                            .orderId(savedShipment.getOrderId())
                            .carrier(savedShipment.getCarrier())
                            .shipmentStatus(savedShipment.getShipmentStatus())
                            .trackingNumber(savedShipment.getTrackingNumber())
                            .build()
            );
            logger.info("Incoming shipment, orderId: " + savedShipment.getOrderId() + ", shipmentId: " + savedShipment.getId());
        });
    }

    @KafkaListener(
            topics = "payment-events",
            groupId = "shipping-service-group"
    )
    @Transactional
    public void listenForPayment(PaymentDTO paymentDTO) {
        logger.info("Received payment message, payment id: "+paymentDTO.getPaymentId());
        if(paymentDTO.getPaymentStatus()== PaymentStatus.PAID){
            String orderId = paymentDTO.getOrderId();
            List<Shipment> shipments = shippingService.getShipmentsByOrderId(orderId);

            shipments.forEach(shipment -> {
                shippingService.processOrder(shipment);
                logger.info("Item in process, orderId: " + shipment.getOrderId() + ", shipmentId: " + shipment.getId());
            });

            shipments = shippingService.getShipmentsByOrderId(orderId);

            shipments.forEach(shipment -> {
                shippingService.shipOrder(shipment);
                logger.info("Item shipped, orderId: " + shipment.getOrderId() + ", shipmentId: " + shipment.getId());
            });

            shipments = shippingService.getShipmentsByOrderId(orderId);

            shipments.forEach(shipment -> {
                shippingService.deliverOrder(shipment);
                logger.info("Item delivered, orderId: " + shipment.getOrderId() + ", shipmentId: " + shipment.getId());
            });
        }
    }

}
