package com.portshop.order_service.service;

import com.portshop.order_service.dto.orderDTO.ItemReservationDTO;
import com.portshop.order_service.dto.orderDTO.OrderDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.List;

@Service
public class InventoryService {

    private final WebClient webClient;

    public InventoryService(
            @Qualifier("inventoryServiceWebClient") WebClient webClient
    ) {
        this.webClient = webClient;
    }

    public List<ItemReservationDTO> reserveItem(OrderDTO orderDTO){
        return webClient.post()
                .uri("/inv/items/reserve-item")
                .bodyValue(orderDTO)
                .retrieve()
                .bodyToFlux(ItemReservationDTO.class)
                .collectList()
                .block();
    }

    public String cancelItemReservation(String orderId){
        return webClient.get()
                .uri("/inv/items/cancel-item/"+orderId)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}
