package com.portshop.shipping_service.dto.orderDTO;

import com.portshop.shipping_service.dto.AddressRequestDTO;
import com.portshop.shipping_service.shippingEnum.Carrier;
import com.portshop.shipping_service.shippingEnum.OrderStatus;
import com.portshop.shipping_service.shippingEnum.PaymentMethod;
import com.portshop.shipping_service.shippingEnum.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private String orderId;
    private String userKeycloakId;
    private OrderStatus orderStatus;
    private String paymentId;
    private BigDecimal subtotal;
    private BigDecimal total;
    private BigDecimal tax;
    private BigDecimal shippingCost;
    private PaymentMethod paymentMethod;
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    private Carrier carrier;
    @Builder.Default
    private List<OrderShipmentDTO> orderShipmentDTOList = new ArrayList<>();
    private AddressRequestDTO billingAddress;
    private AddressRequestDTO shippingAddress;

}
