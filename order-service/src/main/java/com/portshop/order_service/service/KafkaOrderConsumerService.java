package com.portshop.order_service.service;

import com.portshop.order_service.dto.orderDTO.PaymentDTO;
import com.portshop.order_service.dto.orderDTO.ShipmentDTO;
import com.portshop.order_service.exception.ShippingNotFoundException;
import com.portshop.order_service.model.Order;
import com.portshop.order_service.model.Shipment;
import com.portshop.order_service.orderEnum.OrderStatus;
import com.portshop.order_service.orderEnum.PaymentStatus;
import com.portshop.order_service.orderEnum.ShipmentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class KafkaOrderConsumerService {

    private final Logger logger = LoggerFactory.getLogger(KafkaOrderConsumerService.class);
    private final OrderService orderService;
    private final ItemChangeRecordService itemChangeRecordService;

    public KafkaOrderConsumerService(OrderService orderService, ItemChangeRecordService itemChangeRecordService) {
        this.orderService = orderService;
        this.itemChangeRecordService = itemChangeRecordService;
    }

    @KafkaListener(
            topics = "payment-events",
            groupId = "order-service-group"
    )
    @Transactional
    public void listenForPayment(PaymentDTO paymentDTO) {
        logger.info("Received message, payment id: " + paymentDTO.getPaymentId());
        Order order = orderService.getOrder(paymentDTO.getOrderId());
        if(paymentDTO.getPaymentStatus()== PaymentStatus.PAID){
            // add item history for shop searching
            itemChangeRecordService.addItemHistoryByOrder(
                    order,
                    OrderStatus.PENDING,
                    OrderStatus.CONFIRMED,
                    "Payment service",
                    "Item paid"
            );

            order.setOrderStatus(OrderStatus.CONFIRMED);
            order.setPaymentStatus(PaymentStatus.PAID);
            order.setPaymentId(paymentDTO.getPaymentId());
            Order savedOrder = orderService.confirmOrderHistory(order);
            logger.info("Status change to " + OrderStatus.CONFIRMED + ", order id: " + savedOrder.getId());
        } else if(paymentDTO.getPaymentStatus()== PaymentStatus.FAILED) {
            itemChangeRecordService.addItemHistoryByOrder(
                    order,
                    OrderStatus.PENDING,
                    OrderStatus.PENDING,
                    "Payment service",
                    "Payment failed"
            );

            order.setPaymentStatus(PaymentStatus.FAILED);
            order.setPaymentId(paymentDTO.getPaymentId());
            Order savedOrder = orderService.paymentFailedHistory(order);
            logger.info("Payment failed, order id: " + savedOrder.getId());
        }
    }

    @KafkaListener(
            topics = "shipping-events",
            groupId = "order-service-group"
    )
    @Transactional
    public void listenForShipment(ShipmentDTO shipmentDTO) {
        logger.info("Received message, shipment id: " + shipmentDTO.getShipmentId() + " trackingNumber: " + shipmentDTO.getTrackingNumber());
        Order order = orderService.getOrder(shipmentDTO.getOrderId());

        if(shipmentDTO.getShipmentStatus()== ShipmentStatus.PROCESSING){

            Shipment shipment = order.getShipments().stream()
                    .filter(spm ->
                            spm.getId().toString().equals(shipmentDTO.getShipmentId())
                    ).findFirst().orElseThrow(() -> new ShippingNotFoundException(shipmentDTO.getShipmentId()));

            shipment.setTrackingNumber(shipmentDTO.getTrackingNumber());
            shipment.setShipmentStatus(shipmentDTO.getShipmentStatus());

            // add item history for shop searching
            itemChangeRecordService.addItemHistoryByShipment(
                    shipment,
                    OrderStatus.CONFIRMED,
                    OrderStatus.PROCESSING,
                    "Shipping service",
                    "Item processed by post"
            );

            if(order.getOrderStatus() == OrderStatus.CONFIRMED){
                order.setOrderStatus(OrderStatus.PROCESSING);
                order = orderService.processOrderHistory(order);
                logger.info("Item in process by post, order id: " + order.getId() + ", shipment id: " + shipment.getId());
                logger.info("Status change to " + OrderStatus.PROCESSING + ", order id: " + order.getId());
            } else {
                order = orderService.saveOrderWithLog(order,"Shipping service", "Item get processed, order status stays the same");
                logger.info("Item in process by post, order id: " + order.getId() + ", shipment id: " + shipment.getId());
            }
        } else if(shipmentDTO.getShipmentStatus()== ShipmentStatus.SHIPPED){
            Shipment shipment = order.getShipments().stream()
                    .filter(spm ->
                            spm.getId().toString().equals(shipmentDTO.getShipmentId())
                    ).findFirst().orElseThrow(() -> new ShippingNotFoundException(shipmentDTO.getShipmentId()));

            shipment.setShipmentStatus(shipmentDTO.getShipmentStatus());
            shipment.setShippedAt(shipmentDTO.getShippedAt());

            // add item history for shop searching
            itemChangeRecordService.addItemHistoryByShipment(
                    shipment,
                    OrderStatus.PROCESSING,
                    OrderStatus.SHIPPED,
                    "Shipping service",
                    "Item shipped by post"
            );

            if(order.getOrderStatus() == OrderStatus.PROCESSING){
                order.setOrderStatus(OrderStatus.SHIPPED);
                order = orderService.shippingOrderHistory(order);
                logger.info("Item in shipment, order id: " + order.getId() + ", shipment id: " + shipment.getId());
                logger.info("Status change to " + OrderStatus.SHIPPED + ", order id: " + order.getId());
            } else {
                order = orderService.saveOrderWithLog(order,"Shipping service", "Item get shipped, order status stays the same");
                logger.info("Item in shipment, order id: " + order.getId() + ", shipment id: " + shipment.getId());
            }
        } else if(shipmentDTO.getShipmentStatus()== ShipmentStatus.DELIVERED){
            Shipment shipment = order.getShipments().stream()
                    .filter(spm ->
                            spm.getId().toString().equals(shipmentDTO.getShipmentId())
                    ).findFirst().orElseThrow(() -> new ShippingNotFoundException(shipmentDTO.getShipmentId()));

            shipment.setShipmentStatus(shipmentDTO.getShipmentStatus());
            shipment.setDeliveredAt(shipmentDTO.getDeliveredAt());
            Order savedOrder = orderService.saveOrder(order);

            // add item history for shop searching
            itemChangeRecordService.addItemHistoryByShipment(
                    shipment,
                    OrderStatus.SHIPPED,
                    OrderStatus.DELIVERED,
                    "Shipping service",
                    "Item delivered by post"
            );

            // check orderStatus of all shipments are delivered
            boolean allSame = savedOrder.getShipments().stream().allMatch(spm -> spm.getShipmentStatus().equals(ShipmentStatus.DELIVERED));

            if(allSame){
                savedOrder.setOrderStatus(OrderStatus.DELIVERED);
                savedOrder = orderService.deliverOrderHistory(savedOrder);
                logger.info("Item delivered, order id: " + savedOrder.getId() + ", shipment id: " + shipment.getId());
                logger.info("Status change to " + OrderStatus.DELIVERED + ", order id: " + savedOrder.getId());
            } else {
                savedOrder = orderService.saveOrderWithLog(savedOrder,"Shipping service", "Item get delivered, waiting for all orders to be delivered");
                logger.info("Item delivered, order id: " + savedOrder.getId() + ", shipment id: " + shipment.getId());
            }
        }

    }

}
