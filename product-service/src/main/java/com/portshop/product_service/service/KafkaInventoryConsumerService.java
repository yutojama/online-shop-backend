//package com.portshop.product_service.service;
//
//import com.portshop.product_service.dto.orderDTO.OrderDTO;
//import com.portshop.product_service.dto.orderDTO.PaymentDTO;
//import com.portshop.product_service.model.Item;
//import com.portshop.product_service.model.ItemReservation;
//import com.portshop.product_service.productEnum.InventoryStatus;
//import com.portshop.product_service.productEnum.OrderStatus;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Service
//public class KafkaInventoryConsumerService {
//
//    private final Logger logger = LoggerFactory.getLogger(KafkaInventoryConsumerService.class);
//    private final InventoryService inventoryService;
//
//    public KafkaInventoryConsumerService(InventoryService inventoryService) {
//        this.inventoryService = inventoryService;
//    }
//
//    @KafkaListener(
//            topics = "order-submit-events",
//            groupId = "inventory-service-group"
//    )
//    @Transactional
//    public void listenForOrder(OrderDTO orderDTO) {
//        if(orderDTO.getOrderStatus().equals(OrderStatus.PENDING)){
//            logger.info("Received order message, order id: "+orderDTO.getOrderId()+", order products get reserved");
//            orderDTO.getOrderShipmentDTOList().forEach(orderShipmentDTO -> {
//                orderShipmentDTO.getOrderItemDTOList().forEach(orderItemDTO -> {
//                    inventoryService.save(
//                            ItemReservation.builder()
//                                    .itemId(orderItemDTO.getItemId())
//                                    .orderId(orderDTO.getOrderId())
//                                    .quantity(orderItemDTO.getQuantity())
//                                    .status(InventoryStatus.RESERVED)
//                                    .build()
//                    );
//                });
//            });
//        } else if(orderDTO.getOrderStatus().equals(OrderStatus.CANCELLED)){
//            logger.info("Received order message, order id: "+orderDTO.getOrderId()+", order products get cancelled");
//            inventoryService.getItemReservationByOrderId(orderDTO.getOrderId()).forEach(itemReservation -> {
//                itemReservation.setStatus(InventoryStatus.CANCELLED);
//                inventoryService.save(itemReservation);
//            });
//        }
//
//    }
//
//    @KafkaListener(
//            topics = "payment-events",
//            groupId = "inventory-service-group"
//    )
//    @Transactional
//    public void listenForPayment(PaymentDTO paymentDTO) {
//        logger.info("Received payment message, order id: "+paymentDTO.getOrderId()+", order products get confirmed");
//        List<ItemReservation> items = inventoryService.getItemReservationByOrderId(paymentDTO.getOrderId());
//        items.forEach(itemReservation -> {
//            itemReservation.setStatus(InventoryStatus.CONFIRMED);
//            inventoryService.save(itemReservation);
//        });
//    }
//
//}
