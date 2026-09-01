package com.portshop.shipping_service.service;

import com.portshop.shipping_service.dto.ShipmentDTO;
import com.portshop.shipping_service.exception.ShippingExistingException;
import com.portshop.shipping_service.exception.ShippingNotFoundException;
import com.portshop.shipping_service.model.Shipment;
import com.portshop.shipping_service.repository.ShippingRepository;
import com.portshop.shipping_service.shippingEnum.ShipmentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ShippingService {

    private final Logger logger = LoggerFactory.getLogger(ShippingService.class);
    private final ShippingRepository shippingRepository;
    private final KafkaShippingProducerService kafkaShippingProducerService;

    public ShippingService(ShippingRepository shippingRepository, KafkaShippingProducerService kafkaShippingProducerService) {
        this.shippingRepository = shippingRepository;
        this.kafkaShippingProducerService = kafkaShippingProducerService;
    }

    /**
     * shipping DB handling from here
     * */
    public void checkExistingShipment(String shipmentId) {
        if(shippingRepository.existsShipmentByOrderId(shipmentId)){
            throw new ShippingExistingException(shipmentId);
        }
    }

    public Shipment saveShipment(Shipment shipment) {
        return shippingRepository.save(shipment);
    }

    public List<Shipment> getShipmentsByOrderId(String orderId) {
        List<Shipment> shipments = shippingRepository.getShipmentByOrderId(orderId);
        if(shipments.isEmpty()){
            throw new ShippingNotFoundException(" order id: "+orderId);
        }
        return shipments;
    }


    /**
     * shipment handling from here
     * */
    public Shipment processOrder(Shipment shipment) {
        // calling shipping provider
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        shipment.setShipmentStatus(ShipmentStatus.PROCESSING);
        Shipment saveShipment = shippingRepository.save(shipment);

        kafkaShippingProducerService.publishForCreateShipment(ShipmentDTO.builder()
                .shipmentId(saveShipment.getOrderShipmentId())
                .orderId(saveShipment.getOrderId())
                .carrier(saveShipment.getCarrier())
                .shipmentStatus(saveShipment.getShipmentStatus())
                .trackingNumber(saveShipment.getTrackingNumber())
                .build()
        );

        return saveShipment;
    }

    public Shipment shipOrder(Shipment shipment) {
        // calling shipping provider
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        shipment.setShippedAt(Instant.now());
        shipment.setShipmentStatus(ShipmentStatus.SHIPPED);
        Shipment saveShipment = shippingRepository.save(shipment);

        kafkaShippingProducerService.publishForCreateShipment(ShipmentDTO.builder()
                .shipmentId(saveShipment.getOrderShipmentId())
                .orderId(saveShipment.getOrderId())
                .carrier(saveShipment.getCarrier())
                .shipmentStatus(saveShipment.getShipmentStatus())
                .trackingNumber(saveShipment.getTrackingNumber())
                .shippedAt(saveShipment.getShippedAt())
                .build()
        );

        return saveShipment;
    }

    public Shipment deliverOrder(Shipment shipment) {
        // calling shipping provider
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        shipment.setDeliveredAt(Instant.now());
        shipment.setShipmentStatus(ShipmentStatus.DELIVERED);
        Shipment saveShipment = shippingRepository.save(shipment);

        kafkaShippingProducerService.publishForCreateShipment(ShipmentDTO.builder()
                .shipmentId(saveShipment.getOrderShipmentId())
                .orderId(saveShipment.getOrderId())
                .carrier(saveShipment.getCarrier())
                .shipmentStatus(saveShipment.getShipmentStatus())
                .trackingNumber(saveShipment.getTrackingNumber())
                .shippedAt(saveShipment.getShippedAt())
                .deliveredAt(saveShipment.getDeliveredAt())
                .build()
        );

        return saveShipment;
    }

}
