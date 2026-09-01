package com.portshop.order_service.dto;

import com.portshop.order_service.orderEnum.Carrier;
import com.portshop.order_service.orderEnum.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * this dto is used for gathering shipping info before submitting order
 * */

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderShippingInfoRequestDTO {

    @NotNull(message = "Shipping method is required")
    private PaymentMethod paymentMethod;

    @NotNull(message = "Carrier is required")
    private Carrier carrier;

    @NotNull(message = "Shipping address is required")
    private AddressRequestDTO shippingAddress;

    @NotNull(message = "Billing address required")
    private AddressRequestDTO billingAddress;

}
