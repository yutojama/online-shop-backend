package com.portshop.order_service.exception;

import com.portshop.order_service.dto.orderDTO.ItemReservationDTO;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ItemReservationException extends RuntimeException {

    private final List<ItemReservationDTO> itemReservation = new ArrayList<>();

    public ItemReservationException(
            String message,
            List<ItemReservationDTO> itemReservationDTOs
    ) {
        super(message);
        itemReservation.addAll(itemReservationDTOs);
    }

}
